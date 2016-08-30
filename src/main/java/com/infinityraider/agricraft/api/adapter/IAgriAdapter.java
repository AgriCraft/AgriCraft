/*
 */
package com.infinityraider.agricraft.api.adapter;

import java.util.Optional;

/**
 *
 * @author RlonRyan
 */
public interface IAgriAdapter<T> {

    boolean accepts(Object obj);

    Optional<T> valueOf(Object obj);

}
