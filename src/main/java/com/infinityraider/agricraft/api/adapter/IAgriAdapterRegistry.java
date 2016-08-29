/*
 */
package com.infinityraider.agricraft.api.adapter;

import java.util.Optional;

/**
 *
 * @author RlonRyan
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
