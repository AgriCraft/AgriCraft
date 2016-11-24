/*
 */
package com.infinityraider.agricraft.api.adapter;

import java.util.Optional;

/**
 * Interface for determining the true value of certain objects.
 *
 * @param <T> The type adapted by this adapter.
 */
public interface IAgriAdapter<T> {

    boolean accepts(Object obj);

    Optional<T> valueOf(Object obj);

}
