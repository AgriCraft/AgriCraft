package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.items.ItemJournal;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedAnalyzer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSeedAnalyzer extends ContainerAgricraft {
    public TileEntitySeedAnalyzer seedAnalyzer;
    public int progress;
    public static final int seedSlotId = 36;
    public static final int journalSlotId = 37;

    public ContainerSeedAnalyzer(InventoryPlayer inventory, TileEntitySeedAnalyzer seedAnalyzer, int x, int y) {
        super(inventory, x, y);
        this.seedAnalyzer = seedAnalyzer;
        this.addSlots();
    }

    protected void addSlots() {
        //add seed slot to the container
        this.addSlotToContainer(new SlotSeedAnalyzerSeed(seedAnalyzer, seedSlotId, 80, 40));
        //add journal slot to the container
        this.addSlotToContainer(new SlotSeedAnalyzerJournal(seedAnalyzer, journalSlotId, 152, 68));
    }

    public ContainerSeedAnalyzer(InventoryPlayer inventory, TileEntitySeedAnalyzer seedAnalyzer) {
        this(inventory, seedAnalyzer, 8, 94);
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafting) {
        super.addCraftingToCrafters(crafting);
        crafting.sendProgressBarUpdate(this, 0, this.seedAnalyzer.getProgress());
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (Object crafter : this.crafters) {
            ICrafting crafting = (ICrafting) crafter;
            if (this.progress != this.seedAnalyzer.getProgress()) {
                crafting.sendProgressBarUpdate(this, 0, this.seedAnalyzer.getProgress());
            }
        }
        this.progress = this.seedAnalyzer.getProgress();
    }

    @Override
    public void putStackInSlot(int slot, ItemStack stack) {
        switch(slot) {
            case seedSlotId:
                if (TileEntitySeedAnalyzer.isValid(stack) && this.getSlot(slot).isItemValid(stack)) {
                    this.getSlot(slot).putStack(stack);
                }
                return;
            case journalSlotId:
                if (this.getSlot(slot).isItemValid(stack)) {
                    this.getSlot(slot).putStack(stack);
                }
                return;
        }
        super.putStackInSlot(slot, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int type, int newValue) {
        if(type==0) {
            this.seedAnalyzer.setProgress(newValue);
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
            //try to move item from the analyzer into the player's inventory
            if (clickedSlot==seedSlotId || clickedSlot==journalSlotId) {
                if (!this.mergeItemStack(itemstack1, 0, inventorySlots.size() - 2, false)) {
                    return null;
                }
            }
            else {
                //try to move item from the player's inventory into the analyzer
                if(itemstack1.getItem()!=null) {
                    if(TileEntitySeedAnalyzer.isValid(itemstack1)) {
                        if (!this.mergeItemStack(itemstack1, seedSlotId, seedSlotId+1, false)) {
                            return null;
                        }
                    }
                    else if(itemstack1.getItem() instanceof ItemJournal) {
                        if (!this.mergeItemStack(itemstack1, journalSlotId, journalSlotId+1, false)) {
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

    //gets called when you try to merge an itemstack
    @Override
    protected boolean mergeItemStack(ItemStack stack, int start, int stop, boolean backwards)  {
        boolean foundSlot = false;
        int slotIndex = backwards?stop-1:start;
        Slot slot;
        ItemStack stackInSlot;
        //try to stack with existing stacks first
        if (stack.isStackable()) {
            while (stack.stackSize > 0 && (!backwards && slotIndex < stop || backwards && slotIndex >= start)) {
                slot = (Slot)this.inventorySlots.get(slotIndex);
                stackInSlot = slot.getStack();
                if (stackInSlot != null && slot.isItemValid(stack) && stackInSlot.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == stackInSlot.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, stackInSlot)) {
                    int combinedSize = stackInSlot.stackSize + stack.stackSize;
                    if (combinedSize <= stack.getMaxStackSize()) {
                        stack.stackSize = 0;
                        stackInSlot.stackSize = combinedSize;
                        slot.onSlotChanged();
                        foundSlot = true;
                    }
                    else if (stackInSlot.stackSize < stack.getMaxStackSize()) {
                        stack.stackSize -= stack.getMaxStackSize() - stackInSlot.stackSize;
                        stackInSlot.stackSize = stack.getMaxStackSize();
                        slot.onSlotChanged();
                        foundSlot = true;
                    }
                }
                slotIndex = backwards?slotIndex-1:slotIndex+1;
            }
        }
        //put in empty slot
        if (stack.stackSize > 0) {
            slotIndex = backwards?stop-1:start;
            while (!backwards && slotIndex < stop || backwards && slotIndex >= start && !foundSlot) {
                slot = (Slot)this.inventorySlots.get(slotIndex);
                stackInSlot = slot.getStack();
                if (stackInSlot == null && slot.isItemValid(stack)) {
                    slot.putStack(stack.copy());
                    slot.onSlotChanged();
                    stack.stackSize = 0;
                    foundSlot = true;
                    break;
                }
                slotIndex = backwards?slotIndex-1:slotIndex+1;
            }
        }
        return foundSlot;
    }
}
