/*
 */
package com.infinityraider.agricraft.api.v1.stat;

import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author RlonRyan
 */
public interface IAgriStatRegistry {
	
	String NBT_STAT_ID = "agri_stat_id";
	
	default boolean isHandled(NBTTagCompound tag) {
		return isHandled(tag.getString(NBT_STAT_ID));
	}
	
	default boolean isHandled(Class<? extends IAgriStat> clazz) {
		return isHandled(clazz.getCanonicalName());
	}
	
	boolean isHandled(String id);
	
	boolean addStatHandler(Class<? extends IAgriStat> clazz, IAgriStatHandler handler);
	
	boolean removeStatHandler(Class<? extends IAgriStat> clazz);
	
	IAgriStatHandler getStatHandler(NBTTagCompound tag);
	
	IAgriStatHandler getStatHandler(Class<? extends IAgriStat> clazz);
	
	default boolean hasStat(NBTTagCompound tag) {
		IAgriStatHandler handler = getStatHandler(tag);
		return handler != null && handler.isValid(tag);
	}
	
	default IAgriStat getStat(NBTTagCompound tag) {
		IAgriStatHandler handler = getStatHandler(tag);
		return handler == null ? null : handler.getStat(tag);
	}
	
}
