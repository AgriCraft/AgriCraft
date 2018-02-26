/*
 */
package com.infinityraider.agricraft.api.v1.adapter;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for determining the true value of certain objects.
 *
 * @param <T> The type adapted by this adapter.
 */
public interface IAgriAdapter<T> {

    /**
     * Determines if this adapter is capable of converting the given object to the target type.
     *
     * @param obj The object that needs to be converted.
     * @return {@literal true} if this adapter can convert the given object to the target type,
     * {@literal false} otherwise.
     */
    boolean accepts(@Nullable Object obj);

    /**
     * Converts the given object to the target type of this adapter, or returns the empty optional.
     * <p>
     * Notice, implementations of this method should never return null, instead the method should
     * return {@link Optional#empty()}.
     *
     * @param obj
     * @return
     */
    @Nonnull
    Optional<T> valueOf(@Nullable Object obj);

}
