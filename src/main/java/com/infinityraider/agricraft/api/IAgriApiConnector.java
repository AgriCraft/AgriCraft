/*
 */
package com.infinityraider.agricraft.api;

import com.infinityraider.agricraft.api.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.mutation.IAgriMutationEngine;
import com.infinityraider.agricraft.api.mutation.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
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
    Optional<IAgriRegistry<IAgriPlant>> connectPlantRegistry();
    
    @Nonnull
    Optional<IAgriMutationRegistry> connectMutationRegistry();
    
    @Nonnull
    Optional<IAgriSoilRegistry> connectSoilRegistry();
    
    @Nonnull
    Optional<IAgriAdapterizer<IAgriStat>> connectStatRegistry();
    
    @Nonnull
    Optional<IAgriAdapterizer<IAgriStatCalculator>> connectStatCalculatorRegistry();
    
    @Nonnull
    Optional<IAgriMutationEngine> connectMutationEngine();
    
    @Nonnull
    Optional<IAgriAdapterizer<AgriSeed>> connectSeedRegistry();
    
    @Nonnull
    Optional<IAgriAdapterizer<IAgriFertilizer>> connectFertilizerRegistry();
    
}
