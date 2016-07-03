/*
 */
package com.infinityraider.agricraft.api.v1.adapter;

/**
 *
 * @author RlonRyan
 */
public interface IAgriAdapterRegistry<T> {
	
	boolean hasAdapter(IAgriAdapter<T> adapter);
	
	boolean hasAdapter(Object object);
	
	IAgriAdapter<T> getAdapter(Object obj);
	
	boolean registerAdapter(IAgriAdapter<T> adapter);
	
	boolean unregisterAdapter(IAgriAdapter<T> adapter);
	
	default T getValue(Object obj) {
		IAgriAdapter<T> adapter = getAdapter(obj);
		return adapter == null ? null : adapter.getValue(obj);
	}
	
}
