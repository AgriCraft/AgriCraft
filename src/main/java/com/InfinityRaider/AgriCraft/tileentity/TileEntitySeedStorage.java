package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.container.ContainerSeedStorage;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.HashMap;

public class TileEntitySeedStorage extends TileEntityCustomWood implements IInventory{
    public ForgeDirection direction;
    public int usingPlayers;
    private ArrayList<ItemStack> inventory;

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        if(this.direction!=null) {
            tag.setByte("direction", (byte) this.direction.ordinal());
        }
        if(this.inventory!=null && this.inventory.size()>0) {
            NBTTagList tagList = new NBTTagList();
            for(ItemStack seedStack:inventory) {
                NBTTagCompound stackTag = new NBTTagCompound();
                seedStack.writeToNBT(stackTag);
                tagList.appendTag(stackTag);
            }
            tag.setTag(Names.NBT.inventory, tagList);
            tag.setInteger(Names.NBT.size, inventory.size());
        }
        super.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if(tag.hasKey("direction")) {
            this.setDirection(tag.getByte("direction"));
        }
        if(tag.hasKey(Names.NBT.inventory) && tag.hasKey(Names.NBT.size)) {
            this.inventory = new ArrayList<ItemStack>();
            NBTTagList tagList = tag.getTagList(Names.NBT.inventory, tag.getInteger(Names.NBT.size));
            for(int i=0;i<tagList.tagCount();i++) {
                NBTTagCompound stackTag = tagList.getCompoundTagAt(i);
                this.inventory.add(ItemStack.loadItemStackFromNBT(stackTag));
            }
        }
        else {
            this.inventory = null;
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


    //INVENTORY METHODS
    //-----------------
    public ArrayList<ItemStack> getInventory() {
        return this.inventory;
    }

    @Override
    public int getSizeInventory() {
        return this.inventory==null?1:this.inventory.size()+1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        ItemStack stackInSlot = null;
        if(this.inventory!=null && this.inventory.size()>slot) {
            stackInSlot = this.inventory.get(slot).copy();
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
                stackInSlot = stackInSlot.splitStack(amount);
                if(stackInSlot.stackSize==0) {
                    this.inventory.remove(slot);
                }
            }
        }
        return stackInSlot;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack stackInSlot = null;
        if(this.inventory!=null && this.inventory.size()>slot) {
            stackInSlot = this.inventory.get(slot).copy();
            this.inventory.remove(slot);
        }
        return stackInSlot;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack inputStack) {
        if(this.inventory!=null) {
            if(this.inventory.size()>slot) {
                this.inventory.set(slot, inputStack);
            }
            else {
                this.inventory.add(inputStack);
            }
        }
        else {
            this.inventory = new ArrayList<ItemStack>();
            this.inventory.add(inputStack);
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
        boolean allow = false;
        if(stack.getItem() instanceof ItemSeeds) {
            allow = this.inventory==null || this.inventory.size()==0;
            if(!allow) {
                allow = stack.isItemEqual(this.inventory.get(0));
            }
        }
        return allow;
    }
}
