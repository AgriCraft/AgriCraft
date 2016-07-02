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
	
	boolean isHandled(String id);
	
	default boolean addStatHandler(Class<? extends IAgriStat> clazz, IAgriStatHandler handler) {
		return addStatHandler(clazz.getCanonicalName(), handler);
	}
	
	boolean addStatHandler(String id, IAgriStatHandler handler);
	
	default boolean removeStatHandler(Class<? extends IAgriStat> clazz) {
		return removeStatHandler(clazz.getCanonicalName());
	}
	
	boolean removeStatHandler(String id);
	
	default IAgriStatHandler getStatHandler(NBTTagCompound tag) {
		return getStatHandler(tag.getString(NBT_STAT_ID));
	}
	
	default IAgriStatHandler getStatHandler(IAgriStat stat) {
		return getStatHandler(stat.getClass());
	}
	
	default IAgriStatHandler getStatHandler(Class<? extends IAgriStat> clazz) {
		return getStatHandler(clazz.getCanonicalName());
	}
	
	IAgriStatHandler getStatHandler(String id);
	
	default boolean hasStat(NBTTagCompound tag) {
		IAgriStatHandler handler = getStatHandler(tag);
		return handler != null && handler.isValid(tag);
	}
	
	default IAgriStat getStat(NBTTagCompound tag) {
		IAgriStatHandler handler = getStatHandler(tag);
		return handler == null ? null : handler.getStat(tag);
	}
	
}
