/**
 * 
 */
package com.InfinityRaider.AgriCraft.api.v1;

public interface ISeedStats {

	/**
	 * @return The growth value of the seed. Will be -1 if the seed is not analyzed.
	 */
	int getGrowth();
	
	/**
	 * @return The gain value of the seed. Will be -1 if the seed is not analyzed.
	 */
	int getGain();
	
	/**
	 * @return The strength value of the seed. Will be -1 if the seed is not analyzed.
	 */
	int getStrength();
	
	/**
	 * @return The maximum growth value a seed of this kind can have.
	 */
	int getMaxGrowth();
	
	/**
	 * @return The maximum gain value a seed of this kind can have.
	 */
	int getMaxGain();
	
	/**
	 * @return The maximum strength value a seed of this kind can have.
	 */
	int getMaxStrength();


	
}
