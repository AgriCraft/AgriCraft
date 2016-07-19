/*
 */
package com.infinityraider.agricraft.apiimpl;

import java.util.Deque;
import com.infinityraider.agricraft.api.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.adapter.IAgriAdapterRegistry;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * A basic AdapterRegistry implementation.
 *
 * @author RlonRyan
 * @param <T> the type being adapted to.
 */
public class AdapterRegistry<T> implements IAgriAdapterRegistry<T> {

	private final Deque<IAgriAdapter<T>> adapters = new ConcurrentLinkedDeque<>();
	
	@Override
	public boolean hasAdapter(Object obj) {
		return adapters.stream().anyMatch(a -> a.accepts(obj));
	}

	@Override
	public boolean hasAdapter(IAgriAdapter<T> adapter) {
		return this.adapters.contains(adapter);
	}
	
	@Override
	public IAgriAdapter<T> getAdapter(Object obj) {
		for (IAgriAdapter<T> adapter : adapters) {
			if (adapter.accepts(obj)) {
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
