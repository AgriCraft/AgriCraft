package com.InfinityRaider.AgriCraft.tileentity.storage;

import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;
import com.InfinityRaider.AgriCraft.utility.NBTHelper;
import com.InfinityRaider.AgriCraft.utility.interfaces.IDebuggable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public class TileEntitySeedStorage extends TileEntityCustomWood implements ISeedStorageControllable, IDebuggable{
    public ForgeDirection direction;
    public int usingPlayers;
    private ItemSeeds lockedSeed;
    private int lockedSeedMeta;
    private List<NBTTagCompound> tags = new ArrayList<NBTTagCompound>();
    private List<Integer> amounts = new ArrayList<Integer>();
    private ISeedStorageController controller;

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if(this.direction!=null) {
            tag.setByte("direction", (byte) this.direction.ordinal());
        }
        if(this.tags!=null && this.tags.size()>0 && this.lockedSeed!=null) {
            //add the locked seed
            NBTTagCompound seedTag = new NBTTagCompound();
            ItemStack seedStack = new ItemStack(lockedSeed, 1, lockedSeedMeta);
            seedStack.writeToNBT(seedTag);
            tag.setTag(Names.NBT.seed, seedTag);
            //add the slots
            NBTTagList tagList = new NBTTagList();
            for(int i=0;i<tags.size();i++) {
                NBTTagCompound tagAt = tags.get(i);
                int count = amounts.get(i);
                if(tagAt!=null) {
                    //tag
                    NBTTagCompound slotTag = new NBTTagCompound();
                    slotTag.setInteger(Names.NBT.count, count);
                    slotTag.setTag(Names.NBT.tag, tagAt);
                    //add the tag to the list
                    tagList.appendTag(slotTag);
                }
            }
            tag.setTag(Names.NBT.inventory, tagList);
            tag.setInteger(Names.NBT.size, tags.size());
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
        this.tags = new ArrayList<NBTTagCompound>();
        this.amounts = new ArrayList<Integer>();
        if(tag.hasKey(Names.NBT.seed) && tag.hasKey(Names.NBT.inventory) && tag.hasKey(Names.NBT.size)) {
            //read the locked seed
            ItemStack seedStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(Names.NBT.seed));
            this.lockedSeed = (ItemSeeds) seedStack.getItem();
            this.lockedSeedMeta = seedStack.getItemDamage();
            //read the slots
           NBTTagList tagList = tag.getTagList(Names.NBT.inventory, tag.getInteger(Names.NBT.size));
            for(int i=0;i<tagList.tagCount();i++) {
                //get the tag at teh current index
                NBTTagCompound slotTag = tagList.getCompoundTagAt(i);
                //get the data on the tag
                amounts.add(slotTag.getInteger(Names.NBT.count));
                tags.add((NBTTagCompound) slotTag.getTag(Names.NBT.tag));
            }
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
            for(int i=0;i<this.tags.size();i++) {
                ItemStack stack = new ItemStack(this.lockedSeed, amounts.get(i), this.lockedSeedMeta);
                stack.stackTagCompound = tags.get(i);
                stacks.add(stack);
            }
        }
        return stacks;
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
        boolean flag = !this.hasLockedSeed();
        if(!flag) {
            flag = this.tags==null || this.tags.size()==0;
        }
        if(flag) {
            this.lockedSeed = seed;
            this.lockedSeedMeta = meta;
            this.tags = new ArrayList<NBTTagCompound>();
            this.amounts = new ArrayList<Integer>();
            this.markDirty();
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
        return this.tags==null?1:this.tags.size()+1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        slot = slot%1000;
        ItemStack stackInSlot = null;
        if(this.tags!=null && this.tags.size()>slot) {
            stackInSlot = new ItemStack(this.lockedSeed, amounts.get(slot), this.lockedSeedMeta);
        }
        return stackInSlot;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        slot = slot%1000;
        ItemStack stackInSlot = this.getStackInSlot(slot);
        if(stackInSlot!=null) {
            if(stackInSlot.stackSize<=amount) {
                this.tags.set(slot, null);
                this.amounts.set(slot, 0);

            }
            else {
                stackInSlot.stackSize = stackInSlot.stackSize-amount;
                this.amounts.set(slot, stackInSlot.stackSize);
            }
        }
        return stackInSlot;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        slot = slot%1000;
        ItemStack stackInSlot = null;
        if(this.tags!=null) {
            stackInSlot = this.getStackInSlot(slot).copy();
            //this.tags.remove(slot);
            //this.amounts.remove(slot);
        }
        return stackInSlot;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack inputStack) {
        slot = slot%1000;
        if(this.isItemValidForSlot(slot, inputStack)) {
            if (this.tags == null) {
                this.tags = new ArrayList<NBTTagCompound>();
                this.amounts = new ArrayList<Integer>();
            }
            if (this.tags.size() > slot) {
                this.tags.set(slot, inputStack.getTagCompound());
                this.amounts.set(slot, inputStack.stackSize);
            } else {
                this.tags.add(inputStack.stackTagCompound);
                this.amounts.add(inputStack.stackSize);
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
                allow = this.tags.size()<=slot || ItemStack.areItemStackTagsEqual(stack, this.getStackInSlot(slot));
            }
        }
        return allow;
    }

    //Debug method
    @Override
    public void addDebugInfo(List<String> list) {
        String info = this.lockedSeed==null?"null":this.getLockedSeed().getDisplayName();
        list.add("Locked Seed: "+info);
        list.add("Number of seeds: "+this.tags.size());
    }

}
