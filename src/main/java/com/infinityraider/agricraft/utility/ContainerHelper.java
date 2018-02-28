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
        } else if (stack.getCount() <= slotMax) {
            slot.putStack(stack.copy());
            slot.onSlotChanged();
            stack.setCount(0);
            return stack;
        } else {
            slot.putStack(stack.splitStack(1));
            slotAmount = 1;
        }
        
        // The total amount we have.
        final int totalAmount = slotAmount + stack.getCount();
        
        // Calculate the new amounts.
        final int newSlotAmount = Math.min(totalAmount, slotMax);
        final int newStackAmount = totalAmount - newSlotAmount;
        final int delta = newSlotAmount - slotAmount;
        
        // If the amounts have changed, then do an update.
        if (delta > 0) {
            stack.setCount(newStackAmount);
            slot.getStack().setCount(newSlotAmount);
            slot.onSlotChanged();
        }
        
        // Return the stack.
        return stack;
    }
    
    public static final boolean isSlotValidFor(@Nonnull Slot slot, @Nonnull ItemStack stack) {
        // Validate the input parameters.
        Preconditions.checkNotNull(slot);
        Preconditions.checkNotNull(stack);
        
        // If empty then it is easy.
        if (!slot.getHasStack()) {
            return slot.isEnabled() && slot.isItemValid(stack);
        }
        
        // Fetch the slot stack.
        final ItemStack slotStack = slot.getStack();
        
        // Otherwise.
        return slot.isEnabled()
                && slot.isItemValid(stack)
                && (!slotStack.isEmpty() || StackHelper.areCompatible(stack, slotStack));
    }
    
    // Private constructor to prevent instantiation.
    private ContainerHelper() {
        // NOP
    }
    
}
