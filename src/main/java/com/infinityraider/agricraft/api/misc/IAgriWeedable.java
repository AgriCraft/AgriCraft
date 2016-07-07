/*
 */
package com.infinityraider.agricraft.api.misc;

import java.util.Random;

/**
 * An interface for weedable objects.
 *
 * @author RlonRyan
 */
public interface IAgriWeedable {
	
	/**
	 * Determines the chance of the object generating a weed, at random.
	 * 
	 * @return the weed spawn chance.
	 */
	double getWeedSpawnChance();
	
	/**
	 * Determines if the instance is currently overrun with nasty weeds.
	 * 
	 * @return if the instance has weeds in it.
	 */
	boolean hasWeed();

	/**
	 * Spawns weeds in the instance.
	 * 
	 * @return if weeds were spawned in the instance.
	 */
	boolean spawnWeed(Random rand);

	/**
	 * Attempts to spread weeds to neighboring IWeedable instances.
	 * 
	 * @return if weeds were successfully spread.
	 */
	boolean spreadWeed(Random rand);

	/**
	 * Clears weeds from the instance.
	 * 
	 * @return if weeds were cleared from the instance.
	 */
	boolean clearWeed();
	
}
