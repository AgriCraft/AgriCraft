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
import com.infinityraider.agricraft.api.stat.IAgriStatCalculator;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 *
 * @author Ryan
 */
public interface IAgriApiConnector {
    
    @Nonnull
    Optional<IAgriPlantRegistry> connectPlantRegistry();
    
    @Nonnull
    Optional<IAgriMutationRegistry> connectMutationRegistry();
    
    @Nonnull
    Optional<IAgriSoilRegistry> connectSoilRegistry();
    
    @Nonnull
    Optional<IAgriAdapterRegistry<IAgriStat>> connectStatRegistry();
    
    @Nonnull
    Optional<IAgriAdapterRegistry<IAgriStatCalculator>> connectStatCalculatorRegistry();
    
    @Nonnull
    Optional<IAgriMutationEngine> connectMutationEngine();
    
    @Nonnull
    Optional<IAgriAdapterRegistry<AgriSeed>> connectSeedRegistry();
    
    @Nonnull
    Optional<IAgriAdapterRegistry<IAgriFertilizer>> connectFertilizerRegistry();
    
}
