/*
 */
package com.infinityraider.agricraft.apiimpl;

import com.infinityraider.agricraft.api.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.adapter.IAgriAdapterRegistry;
import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * A basic AdapterRegistry implementation.
 *
 * 
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
    public Optional<IAgriAdapter<T>> getAdapter(Object obj) {
        return adapters.stream()
                .filter(a -> a.accepts(obj))
                .findFirst();
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
