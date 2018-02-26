/*
 */
package com.infinityraider.agricraft.api.v1.adapter;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface representing a registry that contains adapters for determining the true value of
 * arbitrary objects.
 *
 * @param <T> The type that this registry adapts to.
 */
public interface IAgriAdapterizer<T> {

    /**
     * Determines if the registry has the given adapter instance.
     *
     * @param adapter The adapter instance to check if present.
     * @return If the registry contains the adapter instance.
     */
    boolean hasAdapter(@Nullable IAgriAdapter<T> adapter);

    /**
     * Determines if the registry has an adapter capable of converting the given object to the
     * target type.
     *
     * @param object The object to find a converter for.
     * @return {@literal true} if the registry has an adapter capable of converting the given
     * object, {@literal false} otherwise.
     */
    boolean hasAdapter(@Nullable Object object);

    /**
     * Fetches the highest priority converter capable of converting the given object, or else the
     * empty optional.
     *
     * @param obj The object to find a converter for.
     * @return The highest priority converter capable of converting the given object, or else the
     * empty optional.
     */
    @Nonnull
    Optional<IAgriAdapter<T>> getAdapter(@Nullable Object obj);

    /**
     * Registers the given adapter instance to the registry.
     * <p>
     * Notice, that per the current implementation, the adapters are assigned priority according to
     * their registration order, whereas the first adapter registered gets assigned the lowest
     * priority, and the last adapter registered will be assigned the highest priority.
     *
     * @param adapter The adapter instance to be registered.
     * @return {@literal true} if the adapter was not already registered and its registration was a
     * success. Otherwise returns {@literal false}.
     */
    boolean registerAdapter(@Nonnull IAgriAdapter<T> adapter);

    /**
     * Removes the given adapter instance from the registry.
     * <p>
     * Notice, that if the registry does not already contain the given adapter instance, the
     * registry will remain unchanged and the method will return false.
     *
     * @param adapter The adapter instance to be unregistered.
     * @return {@literal true} if the adapter was already registered and its removal was a success.
     * Otherwise returns {@literal false}.
     */
    boolean unregisterAdapter(@Nonnull IAgriAdapter<T> adapter);

    /**
     * Determines the value of the given object, using the highest priority adapter that accepts the
     * given object, or else returns the empty optional.
     *
     * @param obj The object to be converted to the target type.
     * @return The value of the converted object, or the empty optional in the case that the
     * conversion failed in some manner.
     */
    @Nonnull
    default Optional<T> valueOf(@Nullable Object obj) {
        return getAdapter(obj).flatMap(a -> a.valueOf(obj));
    }

}
