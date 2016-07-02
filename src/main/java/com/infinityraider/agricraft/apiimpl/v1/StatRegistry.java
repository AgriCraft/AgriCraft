/*
 */
package com.infinityraider.agricraft.apiimpl.v1;

import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import java.util.HashMap;
import java.util.Map;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatHandler;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author RlonRyan
 */
public class StatRegistry implements IAgriStatRegistry {
	
	public static final String NBT_STAT_ID = "agri_stat_id";

	private final Map<String, IAgriStatHandler> handlers;

	public StatRegistry() {
		this.handlers = new HashMap<>();
	}

	public static IAgriStatRegistry getInstance() {
		return APIimplv1.getInstance().getStatRegistry();
	}

	@Override
	public boolean isHandled(NBTTagCompound tag) {
		return handlers.containsKey(tag.getString(NBT_STAT_ID));
	}

	@Override
	public boolean isHandled(Class<? extends IAgriStat> clazz) {
		return handlers.containsKey(clazz.getCanonicalName());
	}

	@Override
	public boolean addStatHandler(Class<? extends IAgriStat> clazz, IAgriStatHandler handler) {
		handlers.put(clazz.getCanonicalName(), handler);
		return true;
	}

	@Override
	public boolean removeStatHandler(Class<? extends IAgriStat> clazz) {
		return handlers.remove(clazz.getCanonicalName()) != null;
	}

	@Override
	public IAgriStatHandler getStatHandler(NBTTagCompound tag) {
		return handlers.get(tag.getString(NBT_STAT_ID));
	}

	@Override
	public IAgriStatHandler getStatHandler(Class<? extends IAgriStat> clazz) {
		return handlers.get(clazz.getCanonicalName());
	}

	@Override
	public boolean setStat(NBTTagCompound tag, IAgriStat stat) {
		IAgriStatHandler handler = getStatHandler(stat.getClass());
		if (handler != null) {
			tag.setString(NBT_STAT_ID, stat.getClass().getCanonicalName());
			handler.setStat(tag, stat);
			return true;
		} else {
			return false;
		}
	}

}
