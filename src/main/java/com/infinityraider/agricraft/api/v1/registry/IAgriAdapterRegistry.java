/*
 */
package com.infinityraider.agricraft.api.v1.registry;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author RlonRyan
 */
public interface IAgriAdapterRegistry<T> {
	
	boolean hasAdapter(IAgriAdapter<T> adapter);
	
	boolean hasAdapter(ItemStack stack);
	
	boolean hasAdapter(NBTTagCompound tag);
	
	IAgriAdapter<T> getAdapter(NBTTagCompound tag);
	
	IAgriAdapter<T> getAdapter(ItemStack stack);
	
	boolean registerAdapter(IAgriAdapter<T> adapter);
	
	boolean unregisterAdapter(IAgriAdapter<T> adapter);
	
	default T getValue(ItemStack stack) {
		IAgriAdapter<T> adapter = getAdapter(stack);
		return adapter == null ? null : adapter.getValue(stack);
	}
	
	default T getValue(NBTTagCompound tag) {
		IAgriAdapter<T> adapter = getAdapter(tag);
		return adapter == null ? null : adapter.getValue(tag);
	}
	
}
