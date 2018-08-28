/*
 */
package com.infinityraider.agricraft.utility;

import com.agricraft.agricore.util.TypeHelper;
import com.google.common.base.Preconditions;
import com.google.common.math.IntMath;
import com.infinityraider.agricraft.api.v1.util.MethodResult;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * A utility class for interacting with ItemStacks in a safe manner that avoids NPE's. This class is
 * required due to the horrible way that ItemStacks are implemented in Minecraft.
 *
 * Ideally, one could convert to using the superior FuzzyStack from the AgriCraft API, but
 * unfortunately that would require a lot of wrapping in order to interact with regular Minecraft
 * code.
 */
public final class StackHelper {

    /**
     * Determines if a given ItemStack is valid. An ItemStack is considered valid if and only if it
     * is not null, its item is not null, and its amount is greater than zero.
     *
     * @param stack the ItemStack to determine if valid.
     * @return {@literal true} if and only if the given ItemStack is considered valid as per the
     * method's description, {@literal false} otherwise.
     */
    public static final boolean isValid(@Nullable ItemStack stack) {
        return (stack != null)
                && (!stack.isEmpty())
                && (stack.getItem() != null);
    }

    /**
     * Determines if a given ItemStack is valid and matches the given Item type. An ItemStack is
     * considered valid if and only if it is not null, its item is not null, its item matches the
     * given type, and its amount is greater than zero.
     *
     * @param stack the ItemStack to determine if valid.
     * @param itemClass the class that the item contained in the ItemStack must match.
     * @return {@literal true} if and only if the given ItemStack is considered valid as per the
     * method's description, {@literal false} otherwise.
     */
    public static final boolean isValid(ItemStack stack, Class<?> itemClass) {
        return (stack != null)
                && (!stack.isEmpty())
                && (TypeHelper.isType(stack.getItem(), itemClass));
    }

    /**
     * Determines if a given ItemStack is valid and matches <em>all</em> the given Item type. An
     * ItemStack is considered valid if and only if it is not null, its item is not null, its item
     * matches <em>all</em> the given types, and its amount is greater than zero.
     *
     * @param stack the ItemStack to determine if valid.
     * @param itemClasses all the classed that the item contained in the ItemStack must match.
     * @return {@literal true} if and only if the given ItemStack is considered valid as per the
     * method's description, {@literal false} otherwise.
     */
    public static final boolean isValid(ItemStack stack, Class<?>... itemClasses) {
        return isValid(stack) && TypeHelper.isAllTypes(stack.getItem(), itemClasses);
    }

    /**
     * Determines if the given ItemStacks are equal, as per the vanilla definition of ItemStack
     * equality ignoring item. Two item stacks, a & b, are considered equal if and only if either a
     * & b are both null or a & b are both non-null and a's item, metadata, and tags equal b's item
     * metadata, and tags. Notice, this method is simply a wrapper of
     * {@link ItemStack#areItemStacksEqual(ItemStack, ItemStack)}, with documentation added based
     * off of bytecode analysis.
     *
     * @param a the stack to compare equality against.
     * @param b the stack to check for equality.
     * @return {@literal true} if and only if a is considered to equivalent to b, {@literal false}
     * otherwise.
     */
    public static final boolean areEqual(@Nullable ItemStack a, @Nullable ItemStack b) {
        return ItemStack.areItemStacksEqual(a, b);
    }

    /**
     * Determines if two ItemStacks are compatible as to be merged.
     * <br>
     * Two ItemStacks, a & b, are considered compatible if and only if:
     * <ul>
     * <li>both are non-null</li>
     * <li>both are non-empty</li>
     * <li>both have the same item</li>
     * <li>both are stackable</li>
     * <li>both have no subtypes or have the same metadata</li>
     * <li>both have the same item as determined by {@link #areItemsEqual()}</li>
     * <li>both have the same damage</li>
     * <li>both have equivalent NBTTags as determined by {@link #areItemStackTagsEqual()}</li>
     * </ul>
     *
     * @param a
     * @param b
     * @return
     */
    public static final boolean areCompatible(@Nonnull ItemStack a, @Nonnull ItemStack b) {
        // Validate
        Preconditions.checkNotNull(a);
        Preconditions.checkNotNull(b);

        // The following is 'borrowed' from ItemHandlerHelper.canItemsStack
        if (a.isEmpty() || b.isEmpty() || a.getItem() != b.getItem()) {
            return false;
        }

        if (!a.isStackable()) {
            return false;
        }

        if (a.getHasSubtypes() && a.getMetadata() != b.getMetadata()) {
            return false;
        }

        if (a.hasTagCompound() != b.hasTagCompound()) {
            return false;
        }

        return (!a.hasTagCompound() || a.getTagCompound().equals(b.getTagCompound())) && a.areCapsCompatible(b);
    }

    /**
     * Determines if the given ItemStacks consist of the same Item, as per the vanilla definition of
     * Item equality. Two ItemStacks have the same Item if and only if they reference the same item
     * instance, and have the same meta values.
     *
     * @param a the stack to compare equality against.
     * @param b the stack to check for equality.
     * @return {@literal true} if and only if the item from stack a is considered to equivalent to
     * the item from stack b, {@literal false} otherwise.
     */
    public static final boolean areItemsEqual(@Nullable ItemStack a, @Nullable ItemStack b) {
        return ItemStack.areItemsEqual(a, b);
    }

    /**
     * Determines if the given ItemStacks consist of the same Item, as per the vanilla definition of
     * Item equality ignoring meta values. Consequently, this method considers two ItemStacks to be
     * equal if and only if they reference the same item instance.
     *
     * @param a the stack to compare equality against.
     * @param b the stack to check for equality.
     * @return {@literal true} if and only if the item from stack a is considered to equivalent to
     * the item from stack b, {@literal false} otherwise.
     */
    public static final boolean areItemsEqualIgnoringMeta(@Nullable ItemStack a, @Nullable ItemStack b) {
        return ItemStack.areItemsEqualIgnoreDurability(a, b);
    }

    /**
     * Determines if the given ItemStack has a non-null NBTTagCompound currently associated with it.
     *
     * @param stack the stack to check if has a non-null NBTTagCompound.
     * @return {@literal true} if and only if the given ItemStack is non-null and
     * ItemStack#getTagCompound() returns a non-null value, {@literal false} otherwise.
     */
    public static final boolean hasTag(@Nullable ItemStack stack) {
        return (stack != null) && (!stack.isEmpty()) && (stack.getTagCompound() != null);
    }

    /**
     * Determines if the given ItemStack has the given key(s) associated with its tag. In the case
     * that the given stack or its tag is null, this method will always return false.
     *
     * @see NBTHelper#hasKey(NBTTagCompound, String...)
     *
     * @param stack the stack to check for the given keys.
     * @param keys the keys to check for in the stack's tag.
     * @return {@literal true} if and only if the stack and its tag are non-null and the tag has all
     * of the given keys, {@literal false} otherwise}.
     */
    public static final boolean hasKey(@Nullable ItemStack stack, @Nullable String... keys) {
        return (stack != null) && (!stack.isEmpty()) && NBTHelper.hasKey(stack.getTagCompound(), keys);
    }

    /**
     * Fetches the tag associated with a given stack, or associates and returns a new NBTTag
     * compound to the stack.
     *
     * @param stack the stack to get the tag from.
     * @return
     */
    @Nonnull
    public static final NBTTagCompound getTag(@Nonnull ItemStack stack) {
        // Ensure the given stack is not null.
        Preconditions.checkNotNull(stack, "The stack to fetch the NBTTagCompound from must not be null!");

        // Attempt to fetch a non-null tag.
        NBTTagCompound tag = stack.getTagCompound();

        // Create new tag, if old one was null.
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }

        // Return the tag.
        return tag;
    }

    /**
     * Breaks up an ItemStack into stacks that obey the maximum size limit set by the contained
     * item.
     *
     * @param stack the stack to break down.
     * @return a list containing the resulting stacks formed by decomposing the given stack into
     * proper-sized stacks.
     */
    @Nonnull
    public static final List<ItemStack> fitToMaxSize(@Nonnull ItemStack stack) {
        // Skip if the stack is empty.
        if (stack.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        // Fetch stack information.
        int stackSize = stack.getCount();
        final int maxSize = stack.getMaxStackSize();

        // Wrap given stack if size is below max size.
        if (stack.getCount() <= maxSize) {
            Arrays.asList(stack);
        }

        // Calculate the number of resulting stacks.
        final int totalStackCount = IntMath.divide(stackSize, maxSize, RoundingMode.UP);

        // Allocate list to hold new stacks.
        final List<ItemStack> stacks = new ArrayList<>(totalStackCount);

        // Loop through stack.
        while (stackSize > maxSize) {
            ItemStack partial = stack.copy();
            partial.setCount(maxSize);
            stackSize = stackSize - maxSize;
            stacks.add(partial);
        }

        // Add last stack if not empty.
        if (stackSize > 0) {
            ItemStack partial = stack.copy();
            partial.setCount(stackSize);
            stacks.add(partial);
        }

        // Return decomposed stack.
        return stacks;
    }

    /**
     * Decreases the size of a given stack by the given amount, if the given player is not in
     * creative mode. Notice, this method treats null players as not being in creative mode, which
     * is important in many automation applications.
     *
     * @param player the player holding the given stack, or null if no player is holding the given
     * stack.
     * @param stack the stack to have its size decreased.
     * @param amount the amount to decrease the stack size by, must be a positive number.
     * @return
     * <table>
     * <tr>
     * <td>{@link MethodResult#PASS}</td>
     * <td>if and only if the stack was not null and the stack size was greater than or equal to the
     * amount to be removed.</td>
     * </tr>
     * <tr>
     * <td>{@link MethodResult#PASS}</td>
     * <td>if and only if the stack was not null and the player is in creative mode.</td>
     * </tr>
     * <tr>
     * <td>{@link MethodResult#FAIL}</td>
     * <td>otherwise.</td>
     * </tr>
     * </table>
     * @throws IllegalArgumentException if the given amount was less than zero.
     */
    @Nonnull
    public static final MethodResult decreaseStackSize(@Nullable EntityPlayer player, @Nonnull ItemStack stack, @Nonnegative int amount) {
        // Validate arguments.
        Preconditions.checkNotNull(stack, "Null Itemstack Detected!");
        Preconditions.checkArgument(amount > 0, "Cannot decrease ItemStack size by a negative amount! %d", amount);

        // Do stuff.
        if (stack.isEmpty()) {
            return MethodResult.FAIL;
        } else if (amount < 0) {
            throw new IllegalArgumentException("" + amount + " is not valid!");
        } else if (player != null && player.isCreative()) {
            return MethodResult.PASS;
        } else if (stack.getCount() - amount < 0) {
            return MethodResult.FAIL;
        } else {
            stack.setCount(stack.getCount() - amount);
            return MethodResult.PASS;
        }
    }
    
    /**
     * Private constructor to prevent utility class initialization.
     */
    private StackHelper() {
        // Nothing to see here.
    }

}
