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
	public boolean addStatHandler(String id, IAgriStatHandler handler) {
		this.handlers.put(id, handler);
		return true;
	}

	@Override
	public boolean removeStatHandler(String id) {
		return this.handlers.remove(id) != null;
	}

	@Override
	public IAgriStatHandler getStatHandler(String id) {
		return this.handlers.get(id);
	}

}
