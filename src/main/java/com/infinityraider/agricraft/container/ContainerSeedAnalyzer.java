package com.infinityraider.agricraft.container;

import com.infinityraider.agricraft.tiles.analyzer.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.utility.StackHelper;
import com.infinityraider.infinitylib.container.ContainerTileBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerSeedAnalyzer extends ContainerTileBase<TileEntitySeedAnalyzer> {

    public int progress;

    public static final int SEED_SLOT_ID = 36;
    public static final int JOURNAL_SLOT_ID = 37;

    public static enum SeedAnalyzerLayout {

        NORMAL(8, 94, 80, 40, 152, 68),
        PERIPHERAL(5, 94, 77, 40, 149, 68);

        public final int offsetX, offsetY;
        public final int seedSlotX, seedSlotY;
        public final int journalSlotX, journalSlotY;

        private SeedAnalyzerLayout(int offsetX, int offsetY, int seedSlotX, int seedSlotY, int journalSlotX, int journalSlotY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.seedSlotX = seedSlotX;
            this.seedSlotY = seedSlotY;
            this.journalSlotX = journalSlotX;
            this.journalSlotY = journalSlotY;
        }

    }

    public ContainerSeedAnalyzer(TileEntitySeedAnalyzer analyzer, InventoryPlayer inventory, SeedAnalyzerLayout layout) {
        super(analyzer, inventory, layout.offsetX, layout.offsetY);

        // Add the seed slot to the container.
        this.addSlotToContainer(new SlotSeedAnalyzerSeed(this.getTile(), SEED_SLOT_ID, layout.seedSlotX, layout.seedSlotY));

        // Add the journal slot to the container.
        this.addSlotToContainer(new SlotSeedAnalyzerJournal(this.getTile(), JOURNAL_SLOT_ID, layout.journalSlotX, layout.journalSlotY));
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener listener : this.listeners) {
            if (this.progress != this.getTile().getProgress()) {
                listener.sendWindowProperty(this, 0, this.getTile().getProgress());
            }
        }
        this.progress = this.getTile().getProgress();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int type, int newValue) {
        if (type == 0) {
            this.getTile().setProgress(newValue);
        }
    }

    public final boolean hasItem(Slot slot) {
        return slot != null && slot.getHasStack();
    }

    //this gets called when a player shift clicks a stack into the inventory
    @Override
    public final ItemStack transferStackInSlot(EntityPlayer player, int clickedSlot) {

        // Get the clicked Slot.
        Slot slot = this.inventorySlots.get(clickedSlot);

        // There is nothing to move!
        if (!hasItem(slot)) {
            return ItemStack.EMPTY;
        }

        // Fetch the itemstack and a copy.
        ItemStack slotStack = slot.getStack();
        final ItemStack itemstack = slotStack.copy();

        // Determine Slot Range
        final int start;
        final int stop;

        if (clickedSlot == SEED_SLOT_ID || clickedSlot == JOURNAL_SLOT_ID) {
            //try to move item from the analyzer into the player's inventory
            start = 0;
            stop = inventorySlots.size() - 2;
        } else {
            //try to move item from the player's inventory into the analyzer
            start = SEED_SLOT_ID;
            stop = JOURNAL_SLOT_ID + 1;
        }

        if (this.mergeItemStack(slotStack, start, stop, false)) {
            if (slotStack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            }
            slot.onSlotChanged();
            slot.onTake(player, slotStack);
            return itemstack;
        }

        return ItemStack.EMPTY;
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
                    int combinedSize = stackInSlot.getCount() + stack.getCount();
                    if (combinedSize <= stack.getMaxStackSize()) {
                        stack.setCount(0);
                        stackInSlot.setCount(combinedSize);
                        slot.onSlotChanged();
                        return true;
                    } else if (stackInSlot.getCount() < stack.getMaxStackSize()) {
                        stack.setCount(combinedSize - stack.getMaxStackSize());
                        stackInSlot.setCount(stack.getMaxStackSize());
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
            if (slot.isItemValid(stack)) {
                slot.putStack(stack.copy());
                slot.onSlotChanged();
                stack.setCount(0);
                return true;
            }
            slotIndex += delta;
        }

        // No open slot was found!
        return false;
    }

}
