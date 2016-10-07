/*
 */
package com.infinityraider.agricraft.api.irrigation;


/**
 *
 * 
 */
public interface IIrrigationAcceptor extends IConnectable {
	
	/**
	 * Determines if a component may accept an amount of water.
	 * 
	 * @param y the y-level of the provided water, in microblocks.
	 * @param amount the amount of fluid to be accepted.
	 * @param partial if partial accepting of fluid is allowed.
	 * @return the amount of water the component may accept, in mB.
	 */
	boolean canAcceptFluid(int y, int amount, boolean partial);
	
	/**
	 * Attempts to provide an amount of water to the given component.
	 * 
	 * @param y the y-level of the provided water, in microblocks.
	 * @param amount the amount of water for the component to accept, in mB.
	 * @param partial if partial accepting of fluid is allowed.
	 * @return the amount of water not accepted, in mB.
	 */
	int acceptFluid(int y, int amount, boolean partial);
	
}
