/*
 * 
 */
package com.infinityraider.agricraft.utility;

import com.google.common.base.Preconditions;
import javax.annotation.Nonnull;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 *
 * @author Ryan
 */
public final class ContainerHelper {

    /**
     * Attempts to merge the given ItemStack into the given slot.
     *
     * @param slot
     * @param stack
     * @return the remainder of the stack, or the empty stack.
     */
    public static final ItemStack attemptMergeIntoSlot(@Nonnull Slot slot, @Nonnull ItemStack stack) {
        // Validate the input parameters.
        Preconditions.checkNotNull(slot);
        Preconditions.checkNotNull(stack);

        // If stack is empty, then return.
        if (stack.isEmpty()) {
            return stack;
        }

        // If the slot is not valid for the stack then fail.
        if (!isSlotValidFor(slot, stack)) {
            return stack;
        }

        // The slot max amount.
        final int slotMax = slot.getItemStackLimit(stack);

        // The amount in the slot.
        final int slotAmount;

        // If the slot is not empty get the amount, else set amount to zero.
        if (slot.getHasStack()) {
            slotAmount = slot.getStack().getCount();
        } else {
            slotAmount = 0;
        }

        // Clear out the slot.
        slot.putStack(ItemStack.EMPTY);

        // The total amount we have.
        final int totalAmount = slotAmount + stack.getCount();

        // Calculate the new amounts.
        final int newSlotAmount = Math.min(totalAmount, slotMax);
        final int newStackAmount = totalAmount - newSlotAmount;

        // Create the new slot stack.
        final ItemStack newSlotStack = stack.copy();
        newSlotStack.setCount(newSlotAmount);

        // If the amounts have changed, then do an update.
        stack.setCount(newStackAmount);
        slot.putStack(newSlotStack);
        slot.onSlotChanged();

        // Return the stack.
        return stack;
    }

    public static final boolean isSlotValidFor(@Nonnull Slot slot, @Nonnull ItemStack stack) {
        // Validate the input parameters.
        Preconditions.checkNotNull(slot);
        Preconditions.checkNotNull(stack);

        // Fetch the slot stack.
        final ItemStack slotStack = slot.getStack();

        // Return
        return slot.isItemValid(stack)
                && (slotStack.isEmpty() || StackHelper.areCompatible(stack, slotStack));
    }

    // Private constructor to prevent instantiation.
    private ContainerHelper() {
        // NOP
    }

}
