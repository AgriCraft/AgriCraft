/*
 */
package com.infinityraider.agricraft.api.v1.stat;

import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author RlonRyan
 */
public interface IAgriStatRegistry {
	
	boolean isHandled(NBTTagCompound tag);
	
	boolean isHandled(Class<? extends IAgriStat> clazz);
	
	boolean addStatHandler(Class<? extends IAgriStat> clazz, IAgriStatHandler handler);
	
	boolean removeStatHandler(Class<? extends IAgriStat> clazz);
	
	IAgriStatHandler getStatHandler(NBTTagCompound tag);
	
	default IAgriStatHandler getStatHandler(IAgriStat stat) {
		return getStatHandler(stat.getClass());
	}
	
	IAgriStatHandler getStatHandler(Class<? extends IAgriStat> clazz);
	
	default boolean hasStat(NBTTagCompound tag) {
		IAgriStatHandler handler = getStatHandler(tag);
		return handler != null && handler.isValid(tag);
	}
	
	default IAgriStat getStat(NBTTagCompound tag) {
		IAgriStatHandler handler = getStatHandler(tag);
		return handler == null ? null : handler.getStat(tag);
	}
	
	boolean setStat(NBTTagCompound tag, IAgriStat stat);
	
}
