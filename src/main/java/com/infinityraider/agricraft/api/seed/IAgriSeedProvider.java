/*
 */
package com.infinityraider.agricraft.api.seed;

import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.api.plant.IAgriPlantProvider;
import com.infinityraider.agricraft.api.stat.IAgriStatProvider;

/**
 * A class for objects containing seeds.
 *
 * @author RlonRyan
 */
public interface IAgriSeedProvider extends IAgriPlantProvider, IAgriStatProvider {

	/**
	 * Determines if the object currently has an associated seed.
	 *
	 * @return if the object has a plant associated with it.
	 */
	default boolean hasSeed() {
		return hasPlant() && hasStat();
	}

	/**
	 * Retrieves the seed associated with this instance.
	 *
	 * @return the seed associated with the instance or null.
	 */
	default AgriSeed getSeed() {
		IAgriPlant plant = getPlant();
		IAgriStat stat = getStat();
		AgriSeed seed = null;
		if (plant != null && stat != null) {
			seed = new AgriSeed(plant, stat);
		}
		return seed;
	}

}
