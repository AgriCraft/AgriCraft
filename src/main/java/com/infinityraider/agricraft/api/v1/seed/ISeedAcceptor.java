/*
 */
package com.infinityraider.agricraft.api.v1.seed;

import com.infinityraider.agricraft.api.API;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plant.IPlantAcceptor;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.stat.IStatAcceptor;
import net.minecraft.item.ItemStack;
import com.infinityraider.agricraft.api.v1.APIv1;

/**
 *
 * @author RlonRyan
 */
public interface ISeedAcceptor extends IPlantAcceptor, IStatAcceptor {
	
	/**
	 * Determines if an seed is valid for this specific instance.
	 * 
	 * @param seed the seed to validate for the instance.
	 * @return if the seed is valid for the instance.
	 */
	default boolean acceptsSeed(ItemStack seed) {
		// Hardcoded, since ok.
		APIv1 api = (APIv1)API.getAPI(3);
		IAgriPlant plant = api.getPlantRegistry().getPlant(seed);
		IAgriStat stat = api.getStats(seed);
		return acceptsPlant(plant) && acceptsStat(stat);
	}
	
	/**
	 * Sets the seed associated with this instance. Should always return the
	 * same result as acceptsSeed() if the plant is invalid.
	 *
	 * @param seed the seed to associate with this instance.
	 * @return if the seed was successfully associated with the instance.
	 */
	default boolean setSeed(ItemStack seed) {
		// Hardcoded, since ok.
		APIv1 api = (APIv1)API.getAPI(3);
		IAgriPlant plant = api.getPlantRegistry().getPlant(seed);
		IAgriStat stat = api.getStats(seed);
		if (acceptsPlant(plant) && acceptsStat(stat)) {
			return setPlant(plant) && setStat(stat);
		} else {
			return false;
		}
	}
	
	/**
	 * Removes the seed associated with this instance.
	 * 
	 * @return the removed seed, or null if no plant was removed.
	 */
	default ItemStack removeSeed() {
		IAgriPlant plant = removePlant();
		IAgriStat stat = removeStat();
		ItemStack seed = null;
		if (plant != null && stat != null) {
			seed = plant.getSeed();
			stat.writeToNBT(seed.getTagCompound());
		}
		return seed;
	}
	
}
