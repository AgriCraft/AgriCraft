/*
 */
package com.infinityraider.agricraft.impl.v1;

import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import java.util.Deque;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A basic AgriAdapterizer implementation.
 *
 *
 * @param <T> the type being adapted to.
 */
public class AgriAdapterizer<T> implements IAgriAdapterizer<T> {

    private final Deque<IAgriAdapter<T>> adapters = new ConcurrentLinkedDeque<>();

    @Override
    public boolean hasAdapter(@Nullable Object obj) {
        return adapters.stream().anyMatch(a -> a.accepts(obj));
    }

    @Override
    public boolean hasAdapter(@Nullable IAgriAdapter<T> adapter) {
        return this.adapters.contains(adapter);
    }

    @Override
    @Nonnull
    public Optional<IAgriAdapter<T>> getAdapter(@Nullable Object obj) {
        return adapters.stream()
                .filter(a -> a.accepts(obj))
                .findFirst();
    }

    @Override
    public boolean registerAdapter(@Nonnull IAgriAdapter<T> adapter) {
        // Validate
        Preconditions.checkNotNull(adapter);

        // Register
        if (!this.adapters.contains(adapter)) {
            this.adapters.push(adapter);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean unregisterAdapter(@Nonnull IAgriAdapter<T> adapter) {
        // Validate
        Preconditions.checkNotNull(adapter);

        // Remove
        return this.adapters.removeFirstOccurrence(adapter);
    }

}
