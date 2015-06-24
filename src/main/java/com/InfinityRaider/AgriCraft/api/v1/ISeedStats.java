/**
 * 
 */
package com.InfinityRaider.AgriCraft.api.v1;

public interface ISeedStats {

	/**
	 * @return The growth value of the seed. Will be -1 if the seed is not analyzed.
	 */
	short getGrowth();
	
	/**
	 * @return The gain value of the seed. Will be -1 if the seed is not analyzed.
	 */
	short getGain();
	
	/**
	 * @return The strength value of the seed. Will be -1 if the seed is not analyzed.
	 */
	short getStrength();
	
	/**
	 * @return The maximum growth value a seed of this kind can have.
	 */
	short getMaxGrowth();
	
	/**
	 * @return The maximum gain value a seed of this kind can have.
	 */
	short getMaxGain();
	
	/**
	 * @return The maximum strength value a seed of this kind can have.
	 */
	short getMaxStrength();


	
}
