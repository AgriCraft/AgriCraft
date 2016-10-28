package com.infinityraider.agricraft.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.blocks.tiles.analyzer.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.utility.StackHelper;
import com.infinityraider.infinitylib.container.ContainerBase;

public class ContainerSeedAnalyzer extends ContainerBase {

    public TileEntitySeedAnalyzer seedAnalyzer;
    public int progress;
    public static final int seedSlotId = 36;
    public static final int journalSlotId = 37;

    public ContainerSeedAnalyzer(InventoryPlayer inventory, TileEntitySeedAnalyzer seedAnalyzer) {
        this(inventory, seedAnalyzer, 8, 94);
    }

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

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener listener : this.listeners) {
            if (this.progress != this.seedAnalyzer.getProgress()) {
                listener.sendProgressBarUpdate(this, 0, this.seedAnalyzer.getProgress());
            }
        }
        this.progress = this.seedAnalyzer.getProgress();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int type, int newValue) {
        if (type == 0) {
            this.seedAnalyzer.setProgress(newValue);
        }
    }

    public final boolean hasItem(Slot slot) {
        return slot != null && StackHelper.isValid(slot.getStack());
    }

    //this gets called when a player shift clicks a stack into the inventory
    @Override
    public final ItemStack transferStackInSlot(EntityPlayer player, int clickedSlot) {

        // Get the clicked Slot.
        Slot slot = this.inventorySlots.get(clickedSlot);

        // There is nothing to move!
        if (!hasItem(slot)) {
            return null;
        }

        // Fetch the itemstack and a copy.
        ItemStack slotStack = slot.getStack();
        final ItemStack itemstack = slotStack.copy();

        // Determine Slot Range
        final int start;
        final int stop;

        if (clickedSlot == seedSlotId || clickedSlot == journalSlotId) {
            //try to move item from the analyzer into the player's inventory
            start = 0;
            stop = inventorySlots.size() - 2;
        } else {
            //try to move item from the player's inventory into the analyzer
            start = seedSlotId;
            stop = journalSlotId + 1;
        }

        if (this.mergeItemStack(slotStack, start, stop, false)) {
            if (slotStack.stackSize == 0) {
                slot.putStack(null);
            }
            slot.onSlotChanged();
            slot.onPickupFromSlot(player, slotStack);
            return itemstack;
        }

        return null;
    }

    //gets called when you try to merge an itemstack
    @Override
    protected final boolean mergeItemStack(ItemStack stack, int start, int stop, boolean backwards) {

        // Ensure Proper Range.
        if (start < 0 || start >= stop) {
            //throw new IndexOutOfBoundsException("The specified slot range is impossible!");
            return false;
        }

        // Test if Valid
        if (!StackHelper.isValid(stack)) {
            return false;
        }

        final int delta = backwards ? -1 : 1;
        int slotIndex = backwards ? stop - 1 : start;
        boolean foundSlot = false;

        //try to stack with existing stacks first
        if (stack.isStackable()) {
            while (slotIndex >= start && slotIndex < stop) {
                Slot slot = this.inventorySlots.get(slotIndex);
                ItemStack stackInSlot = slot.getStack();
                if (slot.isItemValid(stack) && StackHelper.areEqual(stack, stackInSlot)) {
                    int combinedSize = stackInSlot.stackSize + stack.stackSize;
                    if (combinedSize <= stack.getMaxStackSize()) {
                        stack.stackSize = 0;
                        stackInSlot.stackSize = combinedSize;
                        slot.onSlotChanged();
                        return true;
                    } else if (stackInSlot.stackSize < stack.getMaxStackSize()) {
                        stack.stackSize = combinedSize - stack.getMaxStackSize();
                        stackInSlot.stackSize = stack.getMaxStackSize();
                        slot.onSlotChanged();
                        foundSlot = true;
                    }
                }
                slotIndex += delta;
            }
        }
        foundSlot = addToEmptySlot(stack, start, stop, backwards) | foundSlot;
        return foundSlot;
    }

    public final boolean addToEmptySlot(ItemStack stack, int start, int stop, boolean backwards) {

        // Ensure Proper Range
        if (start < 0 || start >= stop) {
            //throw new IndexOutOfBoundsException("The specified slot range is impossible!");
            return false;
        }

        // Test if Valid
        if (!StackHelper.isValid(stack)) {
            return false;
        }

        // Vars
        final int delta = backwards ? -1 : 1;
        int slotIndex = backwards ? stop - 1 : start;

        // Iterate through the slot range searching for an empty stack.
        while (start <= slotIndex && slotIndex < stop) {
            Slot slot = this.inventorySlots.get(slotIndex);
            ItemStack stackInSlot = slot.getStack();
            if (stackInSlot == null && slot.isItemValid(stack)) {
                slot.putStack(stack.copy());
                slot.onSlotChanged();
                stack.stackSize = 0;
                return true;
            }
            slotIndex += delta;
        }

        // No open slot was found!
        return false;
    }

}
