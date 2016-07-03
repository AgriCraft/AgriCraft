/*
 */
package com.infinityraider.agricraft.utility;

import com.agricraft.agricore.util.TypeHelper;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author RlonRyan
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
