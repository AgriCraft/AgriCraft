package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ContainerSeedStorage extends ContainerAgricraft {
    private static final int invOffsetX = 6;
    private static final int invOffsetY = 49;
    private static final int maxVertSlots = 0;
    private static final int maxHorSlots = 14;

    private TileEntitySeedStorage te;

    public ContainerSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(inventory, invOffsetX, invOffsetY);
        //super(inventory, invOffsetX, invOffsetY, maxVertSlots, maxHorSlots);
        this.te = te;
        //add the first #maxHorSlots to the gui
        for(int i=0;i<Math.min(maxHorSlots, te.getSizeInventory()-1);i++) {
            this.addSlotToContainer(new Slot(te, PLAYER_INVENTORY_SIZE+i, invOffsetX+16*i, invOffsetY));
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        te.closeInventory();
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int clickedSlot) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(clickedSlot);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            //try to move item from the container into the player's inventory
            if (clickedSlot>=PLAYER_INVENTORY_SIZE) {
                if (!this.mergeItemStack(itemstack1, 0, 36, false)) {
                    return null;
                }
            }
            else {
                //try to move item from the player's inventory into the container
                if(SeedHelper.isAnalyzedSeed(itemstack1)) {
                    if (this.addSeedToStorage(itemstack1)) {
                        itemstack1.stackSize = 0;
                    } else {
                        return null;
                    }
                }
            }
            if (itemstack1.stackSize == 0) {
                if(slot instanceof SlotSeedStorage) {
                    ((SlotSeedStorage) slot).clearSlot();
                }
                else {
                    slot.putStack(null);
                }
            }
            else {
                slot.onSlotChanged();
            }
            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }
            //it's possible that the slot gets set to null if the complete stack is taken out
            if(slot!=null) {
                slot.onPickupFromSlot(player, itemstack1);
            }
        }
        return itemstack;
    }

    public boolean addSeedToStorage(ItemStack seedStack) {
        boolean success = false;
        if(SeedHelper.isAnalyzedSeed(seedStack)) {
            //first check if it fits into the container
            this.mergeItemStack(seedStack, PLAYER_INVENTORY_SIZE, this.inventorySlots.size(), false);
            if(seedStack.stackSize==0) {
                success = true;
            }
            else if(te.isItemValidForSlot(te.getSizeInventory()-1, seedStack)) {
                te.setInventorySlotContents(te.getSizeInventory()-1, seedStack);
                success = true;
                if(te.getSizeInventory()<maxHorSlots) {
                    int offset = this.inventorySlots.size()-PLAYER_INVENTORY_SIZE;
                    this.addSlotToContainer(new Slot(te, this.inventorySlots.size(), invOffsetX+16*offset, invOffsetY));
                }
            }
        }
        return success;
    }

    /**
     * Tries to merge an itemstack into a range of slots, return true if the stack was (partly) merged
     */
    @Override
    protected boolean mergeItemStack(ItemStack stack, int startSlot, int endSlot, boolean iterateBackwards) {
        boolean flag = false;
        int k = iterateBackwards?endSlot - 1:startSlot;
        Slot currentSlot;
        ItemStack currentStack;
        //look for identical stacks to merge with
        while (stack.stackSize > 0 && (!iterateBackwards && k < endSlot || iterateBackwards && k >= startSlot)) {
            currentSlot = (Slot)this.inventorySlots.get(k);
            currentStack = currentSlot.getStack();
            if (currentStack != null && currentStack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == currentStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, currentStack)) {
                int l = currentStack.stackSize + stack.stackSize;
                //total stacksize is smaller than the limit: merge entire stack into this stack
                if (l <= stack.getMaxStackSize()) {
                    stack.stackSize = 0;
                    currentStack.stackSize = l;
                    currentSlot.onSlotChanged();
                    flag = true;
                }
                //total stacksize exceeds the limit: merge part of the stack into this stack
                else if (currentStack.stackSize < stack.getMaxStackSize()) {
                    stack.stackSize -= stack.getMaxStackSize() - currentStack.stackSize;
                    currentStack.stackSize = stack.getMaxStackSize();
                    currentSlot.onSlotChanged();
                    flag = true;
                }
            }
            k = iterateBackwards?k-1:k+1;
        }
        //couldn't completely merge stack with an existing slot, find the first empty slot to put the rest of the stack in
        if (stack.stackSize > 0) {
            k = iterateBackwards?endSlot-1:startSlot;
            while (!iterateBackwards && k < endSlot || iterateBackwards && k >= startSlot) {
                currentSlot = (Slot)this.inventorySlots.get(k);
                currentStack = currentSlot.getStack();
                if (currentStack == null) {
                    currentSlot.putStack(stack.copy());
                    currentSlot.onSlotChanged();
                    stack.stackSize = 0;
                    flag = true;
                    break;
                }
                k = iterateBackwards?k-1:k+1;
            }
        }
        return flag;
    }
}