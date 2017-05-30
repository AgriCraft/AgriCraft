/*
 */
package com.infinityraider.agricraft.apiimpl;

import com.infinityraider.agricraft.api.IAgriApiConnector;
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

/**
 *
 * @author Ryan
 */
public class AgriApiConnector implements IAgriApiConnector {
    
    private final Optional<IAgriPlantRegistry> plantRegistry;
    private final Optional<IAgriMutationRegistry> mutationRegistry;
    private final Optional<IAgriSoilRegistry> soilRegistry;
    private final Optional<IAgriAdapterRegistry<IAgriStat>> statRegistry;
    private final Optional<IAgriAdapterRegistry<IAgriStatCalculator>> statCalculatorRegistry;
    private final Optional<IAgriMutationEngine> mutationEngine;
    private final Optional<IAgriAdapterRegistry<AgriSeed>> seedRegistry;
    private final Optional<IAgriAdapterRegistry<IAgriFertilizer>> fertilizerRegistry;

    public AgriApiConnector() {
        this.plantRegistry = Optional.of(new PlantRegistry());
        this.mutationRegistry = Optional.of(new MutationRegistry());
        this.soilRegistry = Optional.of(new SoilRegistry());
        this.statRegistry = Optional.of(new AdapterRegistry<>());
        this.statCalculatorRegistry = Optional.of(new AdapterRegistry<>());
        this.mutationEngine = Optional.of(new MutationEngine());
        this.seedRegistry = Optional.of(new AdapterRegistry<>());
        this.fertilizerRegistry = Optional.of(new AdapterRegistry<>());
    }

    @Override
    public Optional<IAgriPlantRegistry> connectPlantRegistry() {
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
    public Optional<IAgriAdapterRegistry<IAgriStat>> connectStatRegistry() {
        return this.statRegistry;
    }

    @Override
    public Optional<IAgriAdapterRegistry<IAgriStatCalculator>> connectStatCalculatorRegistry() {
        return this.statCalculatorRegistry;
    }

    @Override
    public Optional<IAgriMutationEngine> connectMutationEngine() {
        return this.mutationEngine;
    }

    @Override
    public Optional<IAgriAdapterRegistry<AgriSeed>> connectSeedRegistry() {
        return this.seedRegistry;
    }

    @Override
    public Optional<IAgriAdapterRegistry<IAgriFertilizer>> connectFertilizerRegistry() {
        return this.fertilizerRegistry;
    }
    
}
