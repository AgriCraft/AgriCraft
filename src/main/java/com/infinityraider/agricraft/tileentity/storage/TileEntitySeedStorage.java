package com.infinityraider.agricraft.tileentity.storage;

import com.infinityraider.agricraft.api.v1.IDebuggable;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.network.MessageTileEntitySeedStorage;
import com.infinityraider.agricraft.network.NetworkWrapperAgriCraft;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.tileentity.TileEntityCustomWood;
import com.infinityraider.agricraft.utility.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TileEntitySeedStorage extends TileEntityCustomWood implements ISeedStorageControllable, IDebuggable, ISidedInventory {
    private Item lockedSeed;
    private int lockedSeedMeta;
    /** Slots stored in a HashMap to use with Container and with Controller */
    private Map<Integer, SeedStorageSlot> slots = new HashMap<>();
    /** Slots also stored in an ArrayList to use with ISidedInventory */
    private ArrayList<SeedStorageSlot> slotsList = new ArrayList<>();
    private ISeedStorageController controller;

    public TileEntitySeedStorage() {
        super();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if(this.lockedSeed!=null) {
            //add the locked SEED
            NBTTagCompound seedTag = new NBTTagCompound();
            ItemStack seedStack = new ItemStack(lockedSeed, 1, lockedSeedMeta);
            seedStack.writeToNBT(seedTag);
            tag.setTag(AgriCraftNBT.SEED, seedTag);
            if(this.slots!=null) {
                //add the slots
                NBTTagList tagList = new NBTTagList();
                for (Map.Entry<Integer, SeedStorageSlot> entry:slots.entrySet()) {
                    if(entry!=null && entry.getValue()!=null) {
                        SeedStorageSlot slot = entry.getValue();
                        NBTTagCompound stackTag = slot.getTag();
                        //tag
                        NBTTagCompound slotTag = new NBTTagCompound();
                        slotTag.setInteger(AgriCraftNBT.COUNT, slot.count);
                        slotTag.setShort(AgriCraftNBT.GROWTH, stackTag.getShort(AgriCraftNBT.GROWTH));
                        slotTag.setShort(AgriCraftNBT.GAIN, stackTag.getShort(AgriCraftNBT.GAIN));
                        slotTag.setShort(AgriCraftNBT.STRENGTH, stackTag.getShort(AgriCraftNBT.STRENGTH));
                        slotTag.setInteger(AgriCraftNBT.ID, slot.getId());
                        //add the TAG to the list
                        tagList.appendTag(slotTag);
                    }
                }
                tag.setTag(AgriCraftNBT.INVENTORY, tagList);
            }
        }
        if(this.hasController()) {
            NBTHelper.addCoordsToNBT(this.controller.getCoordinates(), tag);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.slots = new HashMap<>();
        this.slotsList = new ArrayList<>();
        if (tag.hasKey(AgriCraftNBT.SEED)) {
            //read the locked SEED
            ItemStack seedStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(AgriCraftNBT.SEED));
            this.lockedSeed = seedStack.getItem();
            this.lockedSeedMeta = seedStack.getItemDamage();
            if (tag.hasKey(AgriCraftNBT.INVENTORY)) {
                //read the slots
                NBTTagList tagList = tag.getTagList(AgriCraftNBT.INVENTORY, 10);
                int invId = this.getControllableID();
                for (int i = 0; i < tagList.tagCount(); i++) {
                    NBTTagCompound slotTag = tagList.getCompoundTagAt(i);
                    NBTTagCompound stackTag = new NBTTagCompound();
                    CropPlantHandler.setSeedNBT(stackTag, slotTag.getShort(AgriCraftNBT.GROWTH), slotTag.getShort(AgriCraftNBT.GAIN), slotTag.getShort(AgriCraftNBT.STRENGTH), true);
                    int id = slotTag.getInteger(AgriCraftNBT.ID);
                    SeedStorageSlot slot = new SeedStorageSlot(stackTag, slotTag.getInteger(AgriCraftNBT.COUNT), id, invId);
                    slots.put(id, slot);
                    slotsList.add(slot);
                }
            }
        } else {
            this.lockedSeed = null;
            this.lockedSeedMeta = 0;
        }
        int[] coords = NBTHelper.getCoordsFromNBT(tag);
        if (coords != null && coords.length == 3) {
            this.controller = (ISeedStorageController) worldObj.getTileEntity(getPos());
        }
    }

    public void syncSlotToClient(SeedStorageSlot slot) {
        NetworkWrapperAgriCraft.wrapper.sendToDimension(new MessageTileEntitySeedStorage(this.getPos(), slot), this.worldObj.provider.getDimensionId());
        this.worldObj.getChunkFromBlockCoords(this.getPos()).setChunkModified();
    }

    //Debug method
    @Override
    public void addDebugInfo(List<String> list) {
        String info = this.lockedSeed==null?"null":this.getLockedSeed().getDisplayName();
        int mapSize = this.slots==null?0:this.slots.size();
        int listSize = this.slotsList==null?0:this.slotsList.size();
        list.add("Locked Seed: " + info);
        list.add("Nr of map entries: " + mapSize);
        list.add("Nr of list entries: " + listSize);
    }

    @Override
    public boolean isRotatable() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addWailaInformation(List information) {
        information.add(StatCollector.translateToLocal("agricraft_tooltip.storage") + ": " + (this.hasLockedSeed() ? getLockedSeed().getDisplayName() : StatCollector.translateToLocal("agricraft_tooltip.none")));
        super.addWailaInformation(information);
    }


    //SEED STORAGE METHODS
    //--------------------
    @Override
    public boolean addStackToInventory(ItemStack stack) {
        boolean success = false;
        if(!CropPlantHandler.isAnalyzedSeed(stack)) {
            return false;
        }
        if(!this.worldObj.isRemote) {
            if (this.hasLockedSeed() && this.lockedSeed == stack.getItem() && this.lockedSeedMeta == stack.getItemDamage()) {
                for (Map.Entry<Integer, SeedStorageSlot> entry : this.slots.entrySet()) {
                    if (entry.getValue() != null) {
                        if (ItemStack.areItemStackTagsEqual(entry.getValue().getStack(this.lockedSeed, this.lockedSeedMeta), stack)) {
                            ItemStack newStack = stack.copy();
                            newStack.stackSize = newStack.stackSize + entry.getValue().count;
                            this.setSlotContents(entry.getKey(), newStack);
                            success = true;
                            break;
                        }
                    }
                }
                if (!success) {
                    if (this.slots.size() == 0) {
                        this.setSlotContents(0, stack);
                        success = true;
                    } else {
                        int slotId = getFirstFreeSlot();
                        if(slotId>=0) {
                            this.setSlotContents(slotId, stack);
                            success = true;
                        }
                    }
                }
            }
            else {
                this.setLockedSeed(stack.getItem(), stack.getItemDamage());
                this.setSlotContents(0, stack);
                success = true;
            }
        }
        return success;
    }

    private int getFirstFreeSlot() {
        for(int i=0;i<slots.size();i++) {
            if(!slots.containsKey(i)) {
                return i;
            }
        }
        return slots.size();
    }

    @Override
    public ItemStack getStackForSlotId(int slotId) {
        if(!this.hasLockedSeed()) {
            return null;
        }
        SeedStorageSlot slot = slots.get(slotId);
        return slot==null?null:slot.getStack(this.lockedSeed, this.lockedSeedMeta);
    }

    @Override
    public void setSlotContents(int realSlotId, ItemStack inputStack) {
        if(realSlotId<0) {
            this.addStackToInventory(inputStack);
            return;
        }
        if(this.isValidForSlot(realSlotId, inputStack)) {
            SeedStorageSlot slotAt = this.slots.get(realSlotId);
            if(slotAt!=null) {
                slotAt.count = inputStack.stackSize;
                if(slotAt.count<=0) {
                    slots.remove(realSlotId);
                    slotsList.remove(slotAt);
                }
            }
            else {
                slotAt = new SeedStorageSlot(inputStack.getTagCompound(), inputStack.stackSize, realSlotId, this.getControllableID());
                if(slotAt.count>0) {
                    this.slots.put(realSlotId, slotAt);
                    this.slotsList.add(slotAt);
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

    private boolean isValidForSlot(int realSlot, ItemStack stack) {
        if(!CropPlantHandler.isAnalyzedSeed(stack)) {
            return false;
        }
        if(this.hasLockedSeed()) {
            if (stack.getItem() == this.lockedSeed && stack.getItemDamage() == this.lockedSeedMeta) {
                SeedStorageSlot slotAt = this.slots.get(realSlot);
                return slotAt == null || ItemStack.areItemStackTagsEqual(stack, slotAt.getStack(lockedSeed, lockedSeedMeta));
            }
        } else {
            return true;
        }
        return false;
    }

    @Override
    public ItemStack decreaseStackSizeInSlot(int realSlotId, int amount) {
        if(!worldObj.isRemote) {
            ItemStack stackInSlot = null;
            if (this.slots != null) {
                SeedStorageSlot slotAt = this.slots.get(realSlotId);
                if (slotAt != null) {
                    stackInSlot = slotAt.getStack(this.lockedSeed, this.lockedSeedMeta);
                    stackInSlot.stackSize = Math.min(amount, slotAt.count);
                    if (slotAt.count <= amount) {
                        this.slots.remove(realSlotId);
                        this.slotsList.remove(slotAt);
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
    public ArrayList<ItemStack> getInventory() {
        ArrayList<ItemStack> stacks = new ArrayList<>();
        if(this.hasLockedSeed()) {
            for(Map.Entry<Integer, SeedStorageSlot> entries:slots.entrySet()) {
                if(entries!=null && entries.getValue()!=null) {
                    stacks.add(entries.getValue().getStack(this.lockedSeed, this.lockedSeedMeta));
                }
            }
        }
        return stacks;
    }

    public List<SeedStorageSlot> getSlots() {
        if(slotsList == null) {
            slotsList = new ArrayList<>();
        }
        return slotsList;
    }

    @Override
    public int[] getControllerCoords() {
        return this.controller!=null?this.controller.getCoordinates():null;
    }

    @Override
    public int[] getCoords() {
        return new int[] {this.xCoord(), this.yCoord(), this.zCoord()};
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
            this.markForUpdate();
        }
    }

    @Override
    public void clearLockedSeed() {
        if(this.slots.size()==0) {
            this.lockedSeed = null;
            this.lockedSeedMeta = 0;
            this.markForUpdate();
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
    @Override
    public int getSizeInventory() {
        //One extra for the 'fake' input only slot
        return this.slots.size()+1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if(slotsList == null || slot >= slotsList.size() || (!this.hasLockedSeed())) {
            return null;
        }
        return slotsList.get(slot).getStack(lockedSeed, lockedSeedMeta);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if(slotsList == null || slot >= slotsList.size() || (!this.hasLockedSeed())) {
            return null;
        }
        if(!worldObj.isRemote) {
            ItemStack stackInSlot = null;
            SeedStorageSlot slotAt = this.slotsList.get(slot);
            if (slotAt != null) {
                stackInSlot = slotAt.getStack(this.lockedSeed, this.lockedSeedMeta);
                stackInSlot.stackSize = Math.min(amount, slotAt.count);
                if (slotAt.count <= amount) {
                    this.slots.remove(slotAt.getId());
                    this.slotsList.remove(slotAt);
                    slotAt.count = 0;
                } else {
                    slotAt.count = slotAt.count - amount;
                }
            }
            this.syncSlotToClient(slotAt);
            return stackInSlot;
        }
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        if (slotsList == null || slot >= slotsList.size() || (!this.hasLockedSeed())) {
            return null;
        }
        SeedStorageSlot slotAt = slotsList.get(slot);
        ItemStack stackInSlot = slotAt.getStack(lockedSeed, lockedSeedMeta);
        this.slots.remove(slotAt.getId());
        this.slotsList.remove(slotAt);
        return stackInSlot;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack inputStack) {
        if(slotsList==null || slot>= slotsList.size()) {
            this.addStackToInventory(inputStack);
            return;
        }
        if(inputStack == null) {
            inputStack = slotsList.get(slot).getStack(lockedSeed, lockedSeedMeta);
            inputStack.stackSize = 0;
        }
        if(this.isItemValidForSlot(slot, inputStack)) {
            SeedStorageSlot slotAt = this.slotsList.get(slot);
            if (slotAt != null) {
                slotAt.count = inputStack.stackSize;
                if (slotAt.count <= 0) {
                    slots.remove(slotAt.getId());
                    slotsList.remove(slotAt);
                }
                if (!this.worldObj.isRemote) {
                    this.syncSlotToClient(slotAt);
                }
            } else {
                this.addStackToInventory(inputStack);
            }
        }
    }

	/**
	 * TODO: FIXME!
	 * @return 
	 */
    @Override
     public String getName() {
        return Reference.MOD_ID+":seed_storage";
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public IChatComponent getDisplayName() {
        return new ChatComponentText(getName());
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        this.slotsList = new ArrayList<>();
        this.slots = new HashMap<>();
        this.lockedSeed = null;
        this.lockedSeedMeta = 0;
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
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if(!CropPlantHandler.isAnalyzedSeed(stack)) {
            return false;
        }
        if(this.hasLockedSeed()) {
            if (stack.getItem() == this.lockedSeed && stack.getItemDamage() == this.lockedSeedMeta) {
                if(slot>=slotsList.size()) {
                    return true;
                }
                SeedStorageSlot slotAt = this.slotsList.get(slot);
                return slotAt == null || ItemStack.areItemStackTagsEqual(stack, this.getStackInSlot(slot));
            }
        } else {
            return true;
        }
        return false;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        int[] array = new int[slotsList.size()+1];
        for(int i=0;i<array.length;i++) {
            array[i] = i;
        }
        return array;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
        if(worldObj.isRemote) {
            return false;
        }
        if(slot>=slotsList.size()) {
            //0 is a virtual slot only used for inputs, this is a workaround to prevent derpy behaviour with code which modifies stacks in slots directly
            return (!this.hasLockedSeed()) || (stack!=null && stack.getItem()==lockedSeed && stack.getItemDamage()==lockedSeedMeta);
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
        if(slot>=slotsList.size()) {
            return false;
        }
        if(!this.hasLockedSeed()) {
            return false;
        }
        if(stack==null || stack.getItem()==null) {
            return false;
        }
        if(stack.getItem()!=lockedSeed || stack.getItemDamage()!=lockedSeedMeta) {
            return false;
        }
        return ItemStack.areItemStackTagsEqual(stack, slotsList.get(slot).getStack(lockedSeed, lockedSeedMeta));
    }
}
