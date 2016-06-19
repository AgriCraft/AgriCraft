/*
 */
package com.infinityraider.agricraft.api.v1.seed;

import com.infinityraider.agricraft.api.v1.plant.IPlantProvider;
import com.infinityraider.agricraft.api.v1.stat.IStatProvider;
import net.minecraft.item.ItemStack;

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
	default ItemStack getSeed() {
		ItemStack seed = null;
		if (hasSeed()) {
			seed = getPlant().getSeed().copy();
			getStat().writeToNBT(seed.getTagCompound());
		}
		return seed;
	}

}
