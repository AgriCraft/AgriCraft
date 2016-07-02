/*
 */
package com.infinityraider.agricraft.api.v1.stat;

import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author RlonRyan
 */
public interface IAgriStatHandler {
	
	boolean isValid(NBTTagCompound tag);
	
	IAgriStat getStat(NBTTagCompound tag);
	
}
