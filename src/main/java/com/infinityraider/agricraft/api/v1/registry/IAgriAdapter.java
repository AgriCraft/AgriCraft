/*
 */
package com.infinityraider.agricraft.api.v1.registry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author RlonRyan
 */
public interface IAgriAdapter<T> {
	
	default boolean accepts(ItemStack stack) {
		return stack.hasTagCompound() && this.accepts(stack.getTagCompound());
	}
	
	boolean accepts(NBTTagCompound tag);
	
	default T getValue(ItemStack stack) {
		return stack.hasTagCompound() ? this.getValue(stack.getTagCompound()) : null;
	}
	
	T getValue(NBTTagCompound tag);
	
}
