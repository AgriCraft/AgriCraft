/*
 */
package com.infinityraider.agricraft.api.adapter;

/**
 *
 * @author RlonRyan
 */
public interface IAgriAdapter<T> {
	
	boolean accepts(Object obj);
	
	T getValue(Object obj);
	
}
