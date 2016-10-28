/*
 */
package com.infinityraider.agricraft.api.adapter;

import java.util.Optional;

/**
 *
 *
 */
public interface IAgriAdapter<T> {

    boolean accepts(Object obj);

    Optional<T> valueOf(Object obj);

}
