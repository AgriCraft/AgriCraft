/*
 */
package com.infinityraider.agricraft.utility;

import net.minecraft.item.ItemStack;

/**
 *
 * @author RlonRyan
 */
public final class StackHelper {

	public static boolean isValid(Class itemClass, ItemStack... stacks) {
		for (ItemStack stack : stacks) {
			if (!isValid(itemClass, stack)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isValid(Class itemClass, ItemStack stack) {
		return stack != null && itemClass.isInstance(stack.getItem());
	}

	public static boolean isValid(ItemStack... stacks) {
		for (ItemStack stack : stacks) {
			if (!isValid(stack)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isValid(ItemStack stack) {
		return stack != null && stack.getItem() != null;
	}

	public static boolean hasTag(ItemStack stack) {
		return stack != null && stack.hasTagCompound();
	}

	public static boolean hasKey(ItemStack stack, String... keys) {
		if (hasTag(stack)) {
			return NBTHelper.hasKey(stack.getTagCompound(), keys);
		}
		return false;
	}

}
