package com.InfinityRaider.AgriCraft.tileentity.storage;

import com.InfinityRaider.AgriCraft.api.v1.IDebuggable;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.network.MessageTileEntitySeedStorage;
import com.InfinityRaider.AgriCraft.network.NetworkWrapperAgriCraft;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import com.InfinityRaider.AgriCraft.utility.NBTHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileEntitySeedStorage extends TileEntityCustomWood implements ISeedStorageControllable, IDebuggable{
    public ForgeDirection direction;
    private Item lockedSeed;
    private int lockedSeedMeta;
    private Map<Integer, SeedStorageSlot> slots = new HashMap<Integer, SeedStorageSlot>();
    private ISeedStorageController controller;

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if(this.direction!=null) {
            tag.setByte("direction", (byte) this.direction.ordinal());
        }
        if(this.lockedSeed!=null) {
            //add the locked seed
            NBTTagCompound seedTag = new NBTTagCompound();
            ItemStack seedStack = new ItemStack(lockedSeed, 1, lockedSeedMeta);
            seedStack.writeToNBT(seedTag);
            tag.setTag(Names.NBT.seed, seedTag);
            if(this.slots!=null) {
                //add the slots
                NBTTagList tagList = new NBTTagList();
                for (Map.Entry<Integer, SeedStorageSlot> entry:slots.entrySet()) {
                    if(entry!=null && entry.getValue()!=null) {
                        SeedStorageSlot slot = entry.getValue();
                        NBTTagCompound stackTag = slot.getTag();
                        //tag
                        NBTTagCompound slotTag = new NBTTagCompound();
                        slotTag.setInteger(Names.NBT.count, slot.count);
                        slotTag.setShort(Names.NBT.growth, stackTag.getShort(Names.NBT.growth));
                        slotTag.setShort(Names.NBT.gain, stackTag.getShort(Names.NBT.gain));
                        slotTag.setShort(Names.NBT.strength, stackTag.getShort(Names.NBT.strength));
                        //add the tag to the list
                        tagList.appendTag(slotTag);
                    }
                }
                tag.setTag(Names.NBT.inventory, tagList);
            }
        }
        if(this.hasController()) {
            NBTHelper.addCoordsToNBT(this.controller.getCoordinates(), tag);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
    super.readFromNBT(tag);
        if(tag.hasKey("direction")) {
            this.setDirection(tag.getByte("direction"));
        }
        this.slots = new HashMap<Integer, SeedStorageSlot>();
        if(tag.hasKey(Names.NBT.seed)) {
            //read the locked seed
            ItemStack seedStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(Names.NBT.seed));
            this.lockedSeed = seedStack.getItem();
            this.lockedSeedMeta = seedStack.getItemDamage();
            if(tag.hasKey(Names.NBT.inventory)) {
                //read the slots
                NBTTagList tagList = tag.getTagList(Names.NBT.inventory, 10);
                int invId = this.getControllableID();
                for (int i = 0; i < tagList.tagCount(); i++) {
                    NBTTagCompound slotTag = tagList.getCompoundTagAt(i);
                    NBTTagCompound stackTag = new NBTTagCompound();
                    SeedHelper.setNBT(stackTag, slotTag.getShort(Names.NBT.growth), slotTag.getShort(Names.NBT.gain), slotTag.getShort(Names.NBT.strength), true);
                    slots.put(i, new SeedStorageSlot(stackTag, slotTag.getInteger(Names.NBT.count), i, invId));
                }
            }
        }
        int[] coords = NBTHelper.getCoordsFromNBT(tag);
        if(coords!=null && coords.length==3) {
            this.controller = (ISeedStorageController) worldObj.getTileEntity(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int data) {
        if(this.worldObj.isRemote) {
            this.decrStackSize(id, data);
        }
        return true;
    }

    //sets the direction based on an int
    public void setDirection(int direction) {
        this.direction = ForgeDirection.getOrientation(direction);
    }

    public void syncSlotToClient(SeedStorageSlot slot) {
        NetworkWrapperAgriCraft.wrapper.sendToDimension(new MessageTileEntitySeedStorage(this.xCoord, this.yCoord, this.zCoord, slot), this.worldObj.provider.dimensionId);
        this.worldObj.getChunkFromBlockCoords(this.xCoord, this.zCoord).setChunkModified();
    }


    //SEED STORAGE METHODS
    //--------------------
    @Override
    public boolean addStackToInventory(ItemStack stack) {
        boolean success = false;
        if(!this.worldObj.isRemote) {
            if (this.hasLockedSeed() && SeedHelper.isAnalyzedSeed(stack) && this.lockedSeed == stack.getItem() && this.lockedSeedMeta == stack.getItemDamage()) {
                int lastId = 0;
                for (Map.Entry<Integer, SeedStorageSlot> entry : this.slots.entrySet()) {
                    lastId = entry.getKey() > lastId ? entry.getKey() : lastId;
                    if (entry.getValue() != null) {
                        if (ItemStack.areItemStackTagsEqual(entry.getValue().getStack(this.lockedSeed, this.lockedSeedMeta), stack)) {
                            this.setInventorySlotContents(entry.getKey(), stack);
                            success = true;
                            break;
                        }
                    }
                }
                if (!success) {
                    if (this.slots.size() == 0) {
                        this.setInventorySlotContents(0, stack);
                    } else {
                        this.setInventorySlotContents(lastId + 1, stack);
                    }
                    success = true;
                }
            }
        }
        return success;
    }

    @Override
    public ArrayList<ItemStack> getInventory() {
        ArrayList<ItemStack> stacks = null;
        if(this.hasLockedSeed()) {
            stacks = new ArrayList<ItemStack>();
            for(Map.Entry<Integer, SeedStorageSlot> entries:slots.entrySet()) {
                if(entries!=null && entries.getValue()!=null) {
                    stacks.add(entries.getValue().getStack(this.lockedSeed, this.lockedSeedMeta));
                }
            }
        }
        return stacks;
    }

    @Override
    public List<SeedStorageSlot> getSlots(Item seed, int meta) {
        ArrayList<SeedStorageSlot> list = new ArrayList<SeedStorageSlot>();
        if(this.lockedSeed!=null && this.lockedSeed==seed && this.lockedSeedMeta==meta) {
            list = new ArrayList<SeedStorageSlot>(slots.values());
        }
        return list;
    }

    @Override
    public int[] getControllerCoords() {
        return this.controller!=null?this.controller.getCoordinates():null;
    }

    @Override
    public int[] getCoords() {
        return new int[] {this.xCoord, this.yCoord, this.zCoord};
    }

    @Override
    public ISeedStorageController getController() {
        return this.controller;
    }

    @Override
    public boolean hasController() {
        return this.controller!=null;
    }

    @Override
    public boolean hasLockedSeed() {
        return this.lockedSeed!=null;
    }

    @Override
    public void setLockedSeed(Item seed, int meta) {
        if(!this.hasLockedSeed()) {
            this.lockedSeed = seed;
            this.lockedSeedMeta = meta;
        }
    }

    @Override
    public ItemStack getLockedSeed() {
        return new ItemStack(this.lockedSeed, 1, this.lockedSeedMeta);
    }

    @Override
    public int getControllableID() {
        int id = -1;
        if(this.hasController()) {
            id = this.getController().getControllableID(this);
        }
        return id;
    }


    //INVENTORY METHODS
    //-----------------
    //Note that I am taking the modulus of 1000 for all the slot indices. This is because a seed has 3 stats with 10 values,
    // so the maximum amount of different slots in the inventory is 1000
    @Override
    public int getSizeInventory() {
        return 1000;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        slot = slot%1000;
        ItemStack stackInSlot = null;
        if(this.slots!=null) {
            SeedStorageSlot slotAt = this.slots.get(slot);
            if(slotAt!=null) {
                stackInSlot = slotAt.getStack(this.lockedSeed, this.lockedSeedMeta);
            }
        }
        return stackInSlot;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if(!worldObj.isRemote) {
            slot = slot % 1000;
            ItemStack stackInSlot = null;
            if (this.slots != null) {
                SeedStorageSlot slotAt = this.slots.get(slot);
                if (slotAt != null) {
                    stackInSlot = slotAt.getStack(this.lockedSeed, this.lockedSeedMeta);
                    stackInSlot.stackSize = Math.min(amount, slotAt.count);
                    if (slotAt.count <= amount) {
                        this.slots.remove(slot);
                        slotAt.count = 0;
                    } else {
                        slotAt.count = slotAt.count - amount;
                    }
                }
                this.syncSlotToClient(slotAt);
            }
            return stackInSlot;
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        slot = slot%1000;
        ItemStack stackInSlot = null;
        if(this.slots!=null) {
            stackInSlot = this.getStackInSlot(slot).copy();
            this.slots.remove(slot);
        }
        return stackInSlot;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack inputStack) {
        slot = slot%1000;
        if(this.isItemValidForSlot(slot, inputStack)) {
            SeedStorageSlot slotAt = this.slots.get(slot);
            if(slotAt!=null) {
                slotAt.count = inputStack.stackSize;
                if(slotAt.count<=0) {
                    slots.remove(slot);
                }
            }
            else {
                slotAt = new SeedStorageSlot(inputStack.getTagCompound(), inputStack.stackSize, slot, this.getControllableID());
                if(slotAt.count>0) {
                    this.slots.put(slot, slotAt);
                }
            }
            if(!this.worldObj.isRemote) {
                this.syncSlotToClient(slotAt);
            }
            else {
                this.markForUpdate();
            }
        }
    }

    @Override
    public String getInventoryName() {
        return Reference.MOD_ID.toLowerCase()+":"+Names.Objects.seedStorage;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (CropPlantHandler.isValidSeed(stack) && this.hasLockedSeed() && stack.getItem() == this.lockedSeed && stack.getItemDamage() == this.lockedSeedMeta && SeedHelper.isAnalyzedSeed(stack)) {
            SeedStorageSlot slotAt = this.slots.get(slot);
            return slotAt == null || ItemStack.areItemStackTagsEqual(stack, this.getStackInSlot(slot));
        }
        return false;
    }

    //Debug method
    @Override
    public void addDebugInfo(List<String> list) {
        String info = this.lockedSeed==null?"null":this.getLockedSeed().getDisplayName();
        int size = this.slots==null?0:this.slots.size();
        list.add("Locked Seed: "+info);
        list.add("Number of seeds: "+size);
    }

}
