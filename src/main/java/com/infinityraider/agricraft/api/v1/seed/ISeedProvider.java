/*
 */
package com.infinityraider.agricraft.api.v1.seed;

import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IPlantProvider;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.stat.IStatProvider;

/**
 * A class for objects containing seeds.
 *
 * @author RlonRyan
 */
public interface ISeedProvider extends IPlantProvider, IStatProvider {

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
