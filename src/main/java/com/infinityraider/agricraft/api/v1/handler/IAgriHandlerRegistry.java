/*
 */
package com.infinityraider.agricraft.api.v1.handler;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author RlonRyan
 */
public interface IAgriHandlerRegistry<T> {
	
	boolean hasHandler(IAgriHandler<T> handler);
	
	boolean addHandler(IAgriHandler<T> handler);
	
	boolean removeHandler(IAgriHandler<T> handler);
	
	default IAgriHandler<T> getHandler(ItemStack stack) {
		return stack.hasTagCompound() ? getHandler(stack.getTagCompound()) : null;
	}
	
	IAgriHandler<T> getHandler(NBTTagCompound tag);
	
	default boolean isValid(ItemStack stack) {
		return stack.hasTagCompound() && isValid(stack.getTagCompound());
	}
	
	default boolean isValid(NBTTagCompound tag) {
		IAgriHandler handler = getHandler(tag);
		return handler != null && handler.isValid(tag);
	}
	
	default T getValue(ItemStack stack) {
		return stack.hasTagCompound() ? getValue(stack.getTagCompound()) : null;
	}
	
	default T getValue(NBTTagCompound tag) {
		IAgriHandler<T> handler = getHandler(tag);
		return handler == null ? null : handler.getValue(tag);
	}
	
}
