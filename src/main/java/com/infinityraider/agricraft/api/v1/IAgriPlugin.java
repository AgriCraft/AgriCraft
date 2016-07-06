/*
 */
package com.infinityraider.agricraft.api.v1;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterRegistry;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlantRegistry;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;

/**
 * Interface for mod plugins to AgriCraft.
 *
 * All classes implementing this interface must have a valid no-args
 * constructor, with which AgriCraft may create the plugin instance.
 *
 * @author RlonRyan
 */
public interface IAgriPlugin {

	boolean isEnabled();
	
	default void preInit() {
		// Default Implementation: Do nothing.
	}
	
	default void init() {
		// Default Implementation: Do nothing.
	}
	
	default void postInit() {
		// Default Implementation: Do nothing.
	}

	default void registerPlants(IAgriPlantRegistry plantRegistry) {
		// Default Implementation: Do nothing.
	}
	
	default void registerMutations(IAgriMutationRegistry mutationRegistry) {
		// Default Implementation: Do nothing.
	}
	
	default void registerStats(IAgriAdapterRegistry<IAgriStat> statRegistry) {
		// Default Implementation: Do nothing.
	}
	
	default void registerSeeds(IAgriAdapterRegistry<AgriSeed> seedRegistry) {
		// Default Implementation: Do nothing.
	}
	
	default void registerFertilizers(IAgriAdapterRegistry<IAgriFertilizer> fertilizerRegistry) {
		// Default Implementation: Do nothing.
	}

}
