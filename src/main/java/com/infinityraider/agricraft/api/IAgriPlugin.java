/*
 */
package com.infinityraider.agricraft.api;

import com.infinityraider.agricraft.api.adapter.IAgriAdapterRegistry;
import com.infinityraider.agricraft.api.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.mutation.IAgriMutationEngine;
import com.infinityraider.agricraft.api.mutation.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.plant.IAgriPlantRegistry;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.api.soil.IAgriSoilRegistry;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.api.stat.IAgriStatCalculatorRegistry;
import java.util.function.Consumer;
import net.minecraft.util.ResourceLocation;

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
	
	default void initPlugin() {
		// Default Implementation: Do nothing.
	}
    
    default void registerSoils(IAgriSoilRegistry soilRegistry) {
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
    
    default void registerStatCalculators(IAgriStatCalculatorRegistry statCalculatorRegistry) {
		// Default Implementation: Do nothing.
	}
    
    default void registerCrossStrategies(IAgriMutationEngine mutationEngine) {
        // Default Implementation: Do nothing.
    }
	
	default void registerSeeds(IAgriAdapterRegistry<AgriSeed> seedRegistry) {
		// Default Implementation: Do nothing.
	}
	
	default void registerFertilizers(IAgriAdapterRegistry<IAgriFertilizer> fertilizerRegistry) {
		// Default Implementation: Do nothing.
	}
	
	default void registerTextures(Consumer<ResourceLocation> textureRegistry) {
		// Default Implementation: Do nothing.
	}

}
