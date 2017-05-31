/*
 */
package com.infinityraider.agricraft.apiimpl;

import com.infinityraider.agricraft.api.IAgriApiConnector;
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

/**
 *
 * @author Ryan
 */
public class AgriApiConnector implements IAgriApiConnector {
    
    private final Optional<IAgriRegistry<IAgriPlant>> plantRegistry;
    private final Optional<IAgriMutationRegistry> mutationRegistry;
    private final Optional<IAgriSoilRegistry> soilRegistry;
    private final Optional<IAgriAdapterizer<IAgriStat>> statRegistry;
    private final Optional<IAgriAdapterizer<IAgriStatCalculator>> statCalculatorRegistry;
    private final Optional<IAgriMutationEngine> mutationEngine;
    private final Optional<IAgriAdapterizer<AgriSeed>> seedRegistry;
    private final Optional<IAgriAdapterizer<IAgriFertilizer>> fertilizerRegistry;

    public AgriApiConnector() {
        this.plantRegistry = Optional.of(new AgriRegistry<>());
        this.mutationRegistry = Optional.of(new AgriMutationRegistry());
        this.soilRegistry = Optional.of(new AgriSoilRegistry());
        this.statRegistry = Optional.of(new AgriAdapterizer<>());
        this.statCalculatorRegistry = Optional.of(new AgriAdapterizer<>());
        this.mutationEngine = Optional.of(new AgriMutationEngine());
        this.seedRegistry = Optional.of(new AgriAdapterizer<>());
        this.fertilizerRegistry = Optional.of(new AgriAdapterizer<>());
    }

    @Override
    public Optional<IAgriRegistry<IAgriPlant>> connectPlantRegistry() {
        return this.plantRegistry;
    }

    @Override
    public Optional<IAgriMutationRegistry> connectMutationRegistry() {
        return this.mutationRegistry;
    }

    @Override
    public Optional<IAgriSoilRegistry> connectSoilRegistry() {
        return this.soilRegistry;
    }

    @Override
    public Optional<IAgriAdapterizer<IAgriStat>> connectStatRegistry() {
        return this.statRegistry;
    }

    @Override
    public Optional<IAgriAdapterizer<IAgriStatCalculator>> connectStatCalculatorRegistry() {
        return this.statCalculatorRegistry;
    }

    @Override
    public Optional<IAgriMutationEngine> connectMutationEngine() {
        return this.mutationEngine;
    }

    @Override
    public Optional<IAgriAdapterizer<AgriSeed>> connectSeedRegistry() {
        return this.seedRegistry;
    }

    @Override
    public Optional<IAgriAdapterizer<IAgriFertilizer>> connectFertilizerRegistry() {
        return this.fertilizerRegistry;
    }
    
}
