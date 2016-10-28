/*
 */
package com.infinityraider.agricraft.utility;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.agricraft.agricore.util.TypeHelper;

/**
 *
 *
 */
public final class StackHelper {

    public static boolean isValid(ItemStack stack) {
        return stack != null && stack.getItem() != null;
    }

    public static boolean isValid(ItemStack... stacks) {
        for (ItemStack stack : stacks) {
            if (!isValid(stack)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValid(ItemStack stack, Class... itemClasses) {
        return isValid(stack) && TypeHelper.isAllTypes(stack.getItem(), itemClasses);
    }

    public static boolean areEqual(ItemStack a, ItemStack b) {
        return isValid(a, b)
                && a.getItem() == b.getItem()
                && (!a.getHasSubtypes() || a.getItemDamage() == b.getItemDamage())
                && ItemStack.areItemStackTagsEqual(a, b);
    }

    public static boolean hasTag(ItemStack stack) {
        return isValid(stack) && stack.hasTagCompound();
    }

    public static boolean hasKey(ItemStack stack, String... keys) {
        return hasTag(stack) && NBTHelper.hasKey(stack.getTagCompound(), keys);
    }

    public static NBTTagCompound getTag(ItemStack stack) {
        if (hasTag(stack)) {
            return stack.getTagCompound();
        } else {
            NBTTagCompound tag = new NBTTagCompound();
            stack.setTagCompound(tag);
            return tag;
        }
    }

}
