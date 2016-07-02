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

	private final Map<String, IAgriStatHandler> handlers;

	public StatRegistry() {
		this.handlers = new HashMap<>();
	}

	public static IAgriStatRegistry getInstance() {
		return APIimplv1.getInstance().getStatRegistry();
	}

	@Override
	public boolean isHandled(String id) {
		return this.handlers.containsKey(id);
	}

	@Override
	public boolean addStatHandler(Class<? extends IAgriStat> clazz, IAgriStatHandler handler) {
		this.handlers.put(clazz.getCanonicalName(), handler);
		return true;
	}

	@Override
	public boolean removeStatHandler(Class<? extends IAgriStat> clazz) {
		return this.handlers.remove(clazz.getCanonicalName()) != null;
	}

	@Override
	public IAgriStatHandler getStatHandler(NBTTagCompound tag) {
		return this.handlers.get(tag.getString(NBT_STAT_ID));
	}

	@Override
	public IAgriStatHandler getStatHandler(Class<? extends IAgriStat> clazz) {
		return this.handlers.get(clazz.getCanonicalName());
	}

}
