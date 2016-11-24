/*
 */
package com.infinityraider.agricraft.api.adapter;

import java.util.Optional;

/**
 * Interface representing a registry that contains adapters for determining the
 * true value of arbitrary objects.
 *
 * @param <T> The type that this registry adapts to.
 */
public interface IAgriAdapterRegistry<T> {

    boolean hasAdapter(IAgriAdapter<T> adapter);

    boolean hasAdapter(Object object);

    Optional<IAgriAdapter<T>> getAdapter(Object obj);

    boolean registerAdapter(IAgriAdapter<T> adapter);

    boolean unregisterAdapter(IAgriAdapter<T> adapter);

    default Optional<T> valueOf(Object obj) {
        return getAdapter(obj).flatMap(a -> a.valueOf(obj));
    }

}
