package com.InfinityRaider.AgriCraft.container;

import com.InfinityRaider.AgriCraft.items.ItemJournal;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedAnalyzer;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

public class ContainerSeedAnalyzer extends Container {
    public TileEntitySeedAnalyzer seedAnalyzer;
    public int progress;
    public static final int seedSlotId = 0;
    public static final int journalSlotId = 1;

    public ContainerSeedAnalyzer(InventoryPlayer inventory, TileEntitySeedAnalyzer seedAnalyzer) {
        this.seedAnalyzer = seedAnalyzer;
        //add seed slot to the container
        this.addSlotToContainer(new SlotSeedAnalyzerSeed(seedAnalyzer, seedSlotId, 80, 40));
        //add journal slot to the container
        this.addSlotToContainer(new SlotSeedAnalyzerJournal(seedAnalyzer, journalSlotId, 152, 68));
        //add player's inventory to the container
        for(int i=0;i<3;i++) {
            for(int j=0;j<9;j++) {
                //new Slot(inventory, slot index, x coordinate, y coordinate)
                this.addSlotToContainer(new Slot(inventory, j+i*9+9, 8+j*18, 94+i*18));
            }
        }
        //add player's hot bar to the container
        for(int i=0;i<9;i++) {
            //new Slot(inventory, slot index, x coordinate, y coordinate)
            this.addSlotToContainer(new Slot(inventory, i, 8 + i*18, 152));
        }
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafting) {
        super.addCraftingToCrafters(crafting);
        crafting.sendProgressBarUpdate(this, 0, this.seedAnalyzer.progress);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (Object crafter : this.crafters) {
            ICrafting crafting = (ICrafting) crafter;
            if (this.progress != this.seedAnalyzer.progress) {
                crafting.sendProgressBarUpdate(this, 0, this.seedAnalyzer.progress);
            }
        }
        this.progress = this.seedAnalyzer.progress;
    }

    @Override
    public void putStackInSlot(int slot, ItemStack stack) {
        switch(slot) {
            case seedSlotId:
                if(TileEntitySeedAnalyzer.isValid(stack) && this.getSlot(slot).isItemValid(stack)) {
                    this.getSlot(slot).putStack(stack);
                }
                break;
            case journalSlotId:
                if(this.getSlot(slot).isItemValid(stack)) {
                    this.getSlot(slot).putStack(stack);
                }
                break;
        }

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int type, int newValue) {
        if(type==0) {
            this.seedAnalyzer.progress = newValue;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
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
            if (clickedSlot==0 || clickedSlot==1) {
                if (!this.mergeItemStack(itemstack1, 2, inventorySlots.size(), false)) {
                    return null;
                }
            }
            else {
                //try to move item from the player's inventory into the analyzer
                if(itemstack1.getItem()!=null) {
                    if(itemstack1.getItem() instanceof ItemSeeds) {
                        if (!SeedHelper.isValidSeed((ItemSeeds) itemstack1.getItem())) {
                            return null;
                        }
                        if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                            return null;
                        }
                    }
                    else if(itemstack1.getItem() instanceof ItemJournal) {
                        if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
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
        int slotIndex = start;
        if (backwards) {
            slotIndex = stop - 1;
        }
        Slot slot;
        ItemStack stackInSlot;
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
                if (backwards) {
                    --slotIndex;
                }
                else {
                    ++slotIndex;
                }
            }
        }
        if (stack.stackSize > 0) {
            if (backwards) {
                slotIndex = stop - 1;
            }
            else {
                slotIndex = start;
            }
            while (!backwards && slotIndex < stop || backwards && slotIndex >= start) {
                slot = (Slot)this.inventorySlots.get(slotIndex);
                stackInSlot = slot.getStack();
                if (stackInSlot == null && slot.isItemValid(stack)) {
                    slot.putStack(stack.copy());
                    slot.onSlotChanged();
                    stack.stackSize = 0;
                    foundSlot = true;
                    break;
                }
                if (backwards) {
                    --slotIndex;
                }
                else {
                    ++slotIndex;
                }
            }
        }
        return foundSlot;
    }
}
