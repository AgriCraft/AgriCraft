/*
 */
package com.infinityraider.agricraft.apiimpl.v1;

import com.infinityraider.agricraft.api.v1.handler.IAgriHandler;
import com.infinityraider.agricraft.api.v1.handler.IAgriHandlerRegistry;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author RlonRyan
 * 
 * @param <T> the type handled by this handler registry.
 */
public class HandlerRegistry<T> implements IAgriHandlerRegistry<T> {

	private final Deque<IAgriHandler<T>> handlers = new ArrayDeque<>();
	
	@Override
	public boolean hasHandler(IAgriHandler<T> handler) {
		return handlers.contains(handler);
	}

	@Override
	public boolean addHandler(IAgriHandler<T> handler) {
		if (!this.handlers.contains(handler)) {
			this.handlers.push(handler);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean removeHandler(IAgriHandler<T> handler) {
		return this.handlers.removeFirstOccurrence(handler);
	}

	@Override
	public IAgriHandler<T> getHandler(NBTTagCompound tag) {
		for(IAgriHandler<T> handler : handlers) {
			if (handler.isValid(tag)) {
				return handler;
			}
		}
		return null;
	}

}
