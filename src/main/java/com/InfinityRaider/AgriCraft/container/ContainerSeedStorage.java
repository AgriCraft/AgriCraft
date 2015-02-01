package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContainerSeedStorage extends ContainerAgricraft {
    public HashMap<Integer, SlotSeedStorage> seedSlots;
    public TileEntitySeedStorage te;
    private int lastSlotId;

    public ContainerSeedStorage(InventoryPlayer inventory, TileEntitySeedStorage te) {
        super(inventory, 82, 94);
        this.lastSlotId = this.PLAYER_INVENTORY_SIZE -1;
        this.te = te;
        this.seedSlots = new HashMap<Integer, SlotSeedStorage>();
        for(ItemStack seedStack:te.getInventory()) {
            this.addNewSlot(seedStack);
        }
    }

    public SlotSeedStorage addNewSlot(ItemStack stack) {
        this.lastSlotId = this.lastSlotId+1;
        SlotSeedStorage newSlot = new SlotSeedStorage(this, this.te, this.lastSlotId, 0, 0, stack);
        this.seedSlots.put(this.lastSlotId, newSlot);
        return newSlot;
    }

    public ArrayList<SlotSeedStorage> getSeedSlots() {
        ArrayList<SlotSeedStorage> slots = new ArrayList<SlotSeedStorage>();
        for(Map.Entry<Integer, SlotSeedStorage> slotEntry:seedSlots.entrySet()) {
            if(slotEntry!=null) {
                slots.add(slotEntry.getValue());
            }
        }
        return slots;
    }

    /**
     * returns a list if itemStacks, for each slot.
     */
    @Override
    public List getInventory() {
        ArrayList arraylist = new ArrayList();
        for (int i = 0; i < this.inventorySlots.size(); ++i) {
            arraylist.add(((Slot)this.inventorySlots.get(i)).getStack());
        }

        return arraylist;
    }

    @Override
    public Slot getSlot(int id) {
        if(id<this.PLAYER_INVENTORY_SIZE) {
            return (Slot) this.inventorySlots.get(id);
        }
        else {
            return this.seedSlots.get(id);
        }
    }

    //this gets called when a player shift clicks a stack into the inventory
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int clickedSlot) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(clickedSlot);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            //try to move item from the container into the player's inventory
            if (clickedSlot>35) {
                if (!this.mergeItemStack(itemstack1, 0, 36, false)) {
                    return null;
                }
            }
            else {
                //try to move item from the player's inventory into the container
                if(itemstack1.getItem()!=null) {
                    if(itemstack1.getItem() instanceof ItemSeeds) {
                        if (!this.mergeItemStack(itemstack1, 36, inventorySlots.size(), false)) {
                            return null;
                        }
                    }
                }
            }
            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            }
            else {
                slot.onSlotChanged();
            }
            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }
            slot.onPickupFromSlot(player, itemstack1);
        }
        return itemstack;
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int startSlot, int endSlot, boolean iterateBackwards) {
        boolean flag = false;
        int k = iterateBackwards?endSlot - 1:startSlot;
        Slot currentSlot;
        ItemStack currentStack;
        while (stack.stackSize > 0 && (!iterateBackwards && k < endSlot || iterateBackwards && k >= startSlot)) {
            currentSlot = (Slot)this.inventorySlots.get(k);
            currentStack = currentSlot.getStack();
            if (currentStack != null && currentStack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == currentStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, currentStack)) {
                int l = currentStack.stackSize + stack.stackSize;
                if (l <= stack.getMaxStackSize()) {
                    stack.stackSize = 0;
                    currentStack.stackSize = l;
                    currentSlot.onSlotChanged();
                    flag = true;
                }
                else if (currentStack.stackSize < stack.getMaxStackSize()) {
                    stack.stackSize -= stack.getMaxStackSize() - currentStack.stackSize;
                    currentStack.stackSize = stack.getMaxStackSize();
                    currentSlot.onSlotChanged();
                    flag = true;
                }
            }
            k = iterateBackwards?k-1:k+1;
        }
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

    public int getNumberOfSlots() {
        return this.lastSlotId-this.PLAYER_INVENTORY_SIZE +1;
    }
}
