/*
 */
package com.infinityraider.agricraft.api.v1.handler;

import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author RlonRyan
 */
public interface IAgriHandler<T> {
	
	boolean isValid(NBTTagCompound tag);
	
	T getValue(NBTTagCompound tag);
	
}
