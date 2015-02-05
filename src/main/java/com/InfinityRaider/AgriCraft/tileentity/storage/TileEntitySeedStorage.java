package com.InfinityRaider.AgriCraft.tileentity.storage;

import com.InfinityRaider.AgriCraft.container.ContainerSeedStorageController;
import com.InfinityRaider.AgriCraft.container.SlotSeedStorage;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import com.InfinityRaider.AgriCraft.utility.NBTHelper;
import com.InfinityRaider.AgriCraft.utility.interfaces.IDebuggable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public class TileEntitySeedStorage extends TileEntityCustomWood implements ISeedStorageControllable, IDebuggable{
    public ForgeDirection direction;
    public int usingPlayers;
    private ItemSeeds lockedSeed;
    private int lockedSeedMeta;
    private ArrayList<SlotSeedStorage> inventory = new ArrayList<SlotSeedStorage>();
    private ISeedStorageController controller;

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if(this.direction!=null) {
            tag.setByte("direction", (byte) this.direction.ordinal());
        }
        if(this.inventory!=null && this.inventory.size()>0 && this.lockedSeed!=null) {
            //add the locked seed
            NBTTagCompound seedTag = new NBTTagCompound();
            ItemStack seedStack = new ItemStack(lockedSeed, 1, lockedSeedMeta);
            seedStack.writeToNBT(seedTag);
            tag.setTag(Names.NBT.seed, seedTag);
            //add the slots
            NBTTagList tagList = new NBTTagList();
            for(SlotSeedStorage slot:inventory) {
                if(slot!=null && slot.getStack()!=null) {
                    //tag for the stack
                    NBTTagCompound slotTag = new NBTTagCompound();
                    slotTag.setInteger(Names.NBT.count, slot.count);
                    slotTag.setTag(Names.NBT.tag, slot.getStack().stackTagCompound);
                    //add the tag to the list
                    tagList.appendTag(slotTag);
                }
            }
            tag.setTag(Names.NBT.inventory, tagList);
            tag.setInteger(Names.NBT.size, inventory.size());
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
        if(tag.hasKey(Names.NBT.seed) && tag.hasKey(Names.NBT.inventory) && tag.hasKey(Names.NBT.size)) {
            //read the locked seed
            ItemStack seedStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(Names.NBT.seed));
            this.lockedSeed = (ItemSeeds) seedStack.getItem();
            this.lockedSeedMeta = seedStack.getItemDamage();
            //read the slots
            this.inventory = new ArrayList<SlotSeedStorage>();
            NBTTagList tagList = tag.getTagList(Names.NBT.inventory, tag.getInteger(Names.NBT.size));
            for(int i=0;i<tagList.tagCount();i++) {
                //get the tag at teh current index
                NBTTagCompound slotTag = tagList.getCompoundTagAt(i);
                //get the data on the tag
                int count = slotTag.getInteger(Names.NBT.count);
                NBTTagCompound stackTag = (NBTTagCompound) slotTag.getTag(Names.NBT.tag);
                //add slot to inventory
                ItemStack slotStack = new ItemStack(this.lockedSeed, count, this.lockedSeedMeta);
                slotStack.stackTagCompound = stackTag;
                this.inventory.add(new SlotSeedStorage(this, this.inventory.size(), slotStack));
            }
        }
        else {
            this.inventory = new ArrayList<SlotSeedStorage>();
        }
        int[] coords = NBTHelper.getCoordsFromNBT(tag);
        if(coords!=null && coords.length==3) {
            this.controller = (ISeedStorageController) worldObj.getTileEntity(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public boolean receiveClientEvent(int eventID, int usingPlayers) {
        if (eventID == 0) {
            this.usingPlayers = usingPlayers;
            return true;
        }
        else  {
            return super.receiveClientEvent(eventID, usingPlayers);
        }
    }

    //sets the direction based on an int
    public void setDirection(int direction) {
        this.direction = ForgeDirection.getOrientation(direction);
    }


    //SEED STORAGE METHODS
    //--------------------
    @Override
    public ArrayList<ItemStack> getInventory() {
        ArrayList<ItemStack> stacks = null;
        if(this.hasLockedSeed()) {
            stacks = new ArrayList<ItemStack>();
            for(SlotSeedStorage slot : this.inventory) {
                stacks.add(slot.getStack());
            }
        }
        return stacks;
    }

    @Override
    public void setInventory(ArrayList<SlotSeedStorage> list) {
        if(list!=null) {
           this.inventory = list;
        }
        else {
            this.inventory = null;
        }
        this.markDirty();
    }

    @Override
    public ArrayList<SlotSeedStorage> getInventorySlots(ContainerSeedStorageController container) {
        return this.inventory;
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
    public void setLockedSeed(ItemSeeds seed, int meta) {
        this.lockedSeed = seed;
        this.lockedSeedMeta = meta;
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
        return this.inventory==null?1:this.inventory.size()+1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        slot = slot%1000;
        ItemStack stackInSlot = null;
        if(this.inventory!=null && this.inventory.size()>slot) {
            stackInSlot = this.inventory.get(slot).getStack();
        }
        return stackInSlot;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        slot = slot%1000;
        ItemStack stackInSlot = this.getStackInSlot(slot);
        if(stackInSlot!=null) {
            if(stackInSlot.stackSize<=amount) {
                this.inventory.remove(slot);
            }
            else {
                stackInSlot.stackSize = stackInSlot.stackSize-amount;
                this.inventory.get(slot).count = stackInSlot.stackSize;
            }
        }
        return stackInSlot;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        slot = slot%1000;
        ItemStack stackInSlot = null;
        if(this.inventory!=null && this.inventory.size()>slot) {
            stackInSlot = this.getStackInSlot(slot).copy();
            this.inventory.remove(slot);
        }
        return stackInSlot;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack inputStack) {
        slot = slot%1000;
        if(this.isItemValidForSlot(slot, inputStack)) {
            if (this.inventory != null) {
                if (this.inventory.size() > slot) {
                    this.inventory.set(slot, new SlotSeedStorage(this, slot, inputStack));
                } else {
                    this.inventory.add(new SlotSeedStorage(this, this.inventory.size(), inputStack));
                }
            } else {
                this.inventory = new ArrayList<SlotSeedStorage>();
                this.inventory.add(new SlotSeedStorage(this, 0, inputStack));
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
        return 0;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {
        this.usingPlayers++;
        this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, Blocks.blockSeedStorage, 0, this.usingPlayers);
    }

    @Override
    public void closeInventory() {
        this.usingPlayers--;
        this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, Blocks.blockSeedStorage, 0, this.usingPlayers);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        boolean allow = this.hasLockedSeed()&& stack.getItem()==this.lockedSeed && stack.getItemDamage()==this.lockedSeedMeta && stack.hasTagCompound();
        if(allow) {
            allow = stack.stackTagCompound.hasKey(Names.NBT.analyzed) && stack.stackTagCompound.getBoolean(Names.NBT.analyzed);
            if(allow) {
                allow = this.inventory.size()<=slot || ItemStack.areItemStackTagsEqual(stack, this.getStackInSlot(slot));
            }
        }
        return allow;
    }

    //Debug method
    @Override
    public void addDebugInfo(List<String> list) {
        list.add("Locked Seed: "+this.getLockedSeed().getDisplayName());
    }

}
