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
    private ArrayList<SeedStorageSlot> inventory = new ArrayList<SeedStorageSlot>();
    private int[] controllerCoords;

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
            for(SeedStorageSlot slot:inventory) {
                if(slot!=null && slot.tag!=null) {
                    //tag for the stack
                    NBTTagCompound slotTag = new NBTTagCompound();
                    slotTag.setInteger(Names.NBT.count, slot.count);
                    slotTag.setTag(Names.NBT.tag, slot.tag);
                    //add the tag to the list
                    tagList.appendTag(slotTag);
                }
            }
            tag.setTag(Names.NBT.inventory, tagList);
            tag.setInteger(Names.NBT.size, inventory.size());
        }
        if(this.hasController()) {
            NBTHelper.addCoordsToNBT(controllerCoords[0], controllerCoords[1], controllerCoords[2], tag);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if(tag.hasKey("direction")) {
            this.setDirection(tag.getByte("direction"));
        }
        if(tag.hasKey(Names.NBT.seed) && tag.hasKey(Names.NBT.inventory) && tag.hasKey(Names.NBT.size)) {
            //read the locked seed
            ItemStack seedStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(Names.NBT.seed));
            this.lockedSeed = (ItemSeeds) seedStack.getItem();
            this.lockedSeedMeta = seedStack.getItemDamage();
            //read the slots
            this.inventory = new ArrayList<SeedStorageSlot>();
            NBTTagList tagList = tag.getTagList(Names.NBT.inventory, tag.getInteger(Names.NBT.size));
            for(int i=0;i<tagList.tagCount();i++) {
                //get the tag at teh current index
                NBTTagCompound slotTag = tagList.getCompoundTagAt(i);
                //get the data on the tag
                int count = slotTag.getInteger(Names.NBT.count);
                NBTTagCompound stackTag = (NBTTagCompound) slotTag.getTag(Names.NBT.tag);
                //add slot to inventory
                this.inventory.add(new SeedStorageSlot(stackTag, count));
            }
        }
        else {
            this.inventory = new ArrayList<SeedStorageSlot>();
        }
        int[] coords = NBTHelper.getCoordsFromNBT(tag);
        if(coords!=null && coords.length==3) {
            this.controllerCoords = coords;
        }
        super.readFromNBT(tag);
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
    public ArrayList<SeedStorageSlot> getInventorySlots() {
        return this.inventory;
    }

    @Override
    public ArrayList<ItemStack> getInventory() {
        ArrayList<ItemStack> stacks = null;
        if(this.hasLockedSeed()) {
            stacks = new ArrayList<ItemStack>();
            for(SeedStorageSlot slot : this.inventory) {
                ItemStack newStack = new ItemStack(this.lockedSeed, slot.count, this.lockedSeedMeta);
                newStack.stackTagCompound = slot.tag;
                stacks.add(newStack);
            }
        }
        return stacks;
    }

    @Override
    public void setInventory(ArrayList<SlotSeedStorage> list) {
        if(list!=null) {
            this.inventory = new ArrayList<SeedStorageSlot>();
            if(this.hasLockedSeed()) {
                for (SlotSeedStorage slot : list) {
                    if (slot.getStack().getItem() == this.lockedSeed && slot.getStack().getItemDamage() == this.lockedSeedMeta) {
                        this.inventory.add(new SeedStorageSlot(slot.getStack().stackTagCompound, slot.count));
                    }
                }
            }
        }
        else {
            this.inventory = null;
        }
        this.markDirty();
    }

    @Override
    public ArrayList<SlotSeedStorage> getInventorySlots(ContainerSeedStorageController container) {
        ArrayList<SlotSeedStorage> slots = null;
        if(this.hasLockedSeed()) {
            slots = new ArrayList<SlotSeedStorage>();
            for(int i=0;i<this.inventory.size();i++) {
                slots.add(new SlotSeedStorage(container, this, this.getControllableID()*1000+i, 0, 0, this.getStackInSlot(i)));
            }
        }
        return slots;
    }

    @Override
    public int[] getControllerCoords() {
        return this.controllerCoords;
    }

    @Override
    public int[] getCoords() {
        return new int[] {this.xCoord, this.yCoord, this.zCoord};
    }

    @Override
    public ISeedStorageController getController() {
        TileEntity te = this.worldObj.getTileEntity(this.controllerCoords[0], this.controllerCoords[1], this.controllerCoords[2]);
        return (te instanceof ISeedStorageController)? (ISeedStorageController) te:null;
    }

    @Override
    public boolean hasController() {
        return this.controllerCoords!=null && (this.worldObj.getTileEntity(this.controllerCoords[0], this.controllerCoords[1], this.controllerCoords[2]) instanceof ISeedStorageController);
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
    @Override
    public int getSizeInventory() {
        return this.inventory==null?1:this.inventory.size()+1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        ItemStack stackInSlot = null;
        if(this.inventory!=null && this.inventory.size()>slot) {
            stackInSlot = new ItemStack(this.lockedSeed, this.inventory.get(slot).count, this.lockedSeedMeta);
            stackInSlot.stackTagCompound = this.inventory.get(slot).tag;
        }
        return stackInSlot;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
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
        ItemStack stackInSlot = null;
        if(this.inventory!=null && this.inventory.size()>slot) {
            stackInSlot = this.getStackInSlot(slot).copy();
            this.inventory.remove(slot);
        }
        return stackInSlot;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack inputStack) {
        if(this.isItemValidForSlot(slot, inputStack)) {
            SeedStorageSlot newSlot = new SeedStorageSlot(inputStack.stackTagCompound, inputStack.stackSize);
            if (this.inventory != null) {
                if (this.inventory.size() > slot) {
                    this.inventory.set(slot, newSlot);
                } else {
                    this.inventory.add(newSlot);
                }
            } else {
                this.inventory = new ArrayList<SeedStorageSlot>();
                this.inventory.add(newSlot);
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
