/*
 */
package com.infinityraider.agricraft.api.misc;

/**
 * An interface for weedable objects.
 *
 * @author RlonRyan
 */
public interface IAgriWeedable {
	
	/**
	 * Determines if the instance is currently overrun with nasty weeds, and may be weeded.
	 * 
	 * @return if the instance has weeds in it, and can be weeded.
	 */
	boolean canWeed();

	/**
	 * Clears weeds from the instance.
	 * 
	 * @return if weeds were cleared from the instance.
	 */
	boolean clearWeed();
	
}
