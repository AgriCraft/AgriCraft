/*
 */
package com.infinityraider.agricraft.apiimpl.v1;

import net.minecraft.item.ItemStack;
import java.util.ArrayDeque;
import java.util.Deque;
import com.infinityraider.agricraft.api.v1.registry.IAgriAdapter;
import com.infinityraider.agricraft.api.v1.registry.IAgriAdapterRegistry;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author RlonRyan
 */
public class AdapterRegistry<T> implements IAgriAdapterRegistry<T> {

	private final Deque<IAgriAdapter<T>> adapters = new ArrayDeque<>();

	@Override
	public boolean hasAdapter(ItemStack stack) {
		for (IAgriAdapter<T> adapter : adapters) {
			if (adapter.accepts(stack)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean hasAdapter(NBTTagCompound tag) {
		for (IAgriAdapter<T> adapter : adapters) {
			if (adapter.accepts(tag)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasAdapter(IAgriAdapter<T> adapter) {
		return this.adapters.contains(adapter);
	}

	@Override
	public IAgriAdapter<T> getAdapter(ItemStack stack) {
		for (IAgriAdapter<T> adapter : adapters) {
			if (adapter.accepts(stack)) {
				return adapter;
			}
		}
		return null;
	}
	
	@Override
	public IAgriAdapter<T> getAdapter(NBTTagCompound tag) {
		for (IAgriAdapter<T> adapter : adapters) {
			if (adapter.accepts(tag)) {
				return adapter;
			}
		}
		return null;
	}

	@Override
	public boolean registerAdapter(IAgriAdapter<T> adapter) {
		if (!this.adapters.contains(adapter)) {
			this.adapters.push(adapter);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean unregisterAdapter(IAgriAdapter<T> adapter) {
		return this.adapters.removeFirstOccurrence(adapter);
	}

}
