/*
 */
package com.infinityraider.agricraft.impl.v1;

import com.infinityraider.agricraft.api.v1.AgriApiState;
import com.infinityraider.agricraft.api.v1.IAgriApiConnector;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutationEngine;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutationRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoilRegistry;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatCalculator;

/**
 *
 * @author Ryan
 */
public class AgriApiConnector implements IAgriApiConnector {
    
    private final IAgriRegistry<IAgriPlant> plantRegistry;
    private final IAgriMutationRegistry mutationRegistry;
    private final IAgriSoilRegistry soilRegistry;
    private final IAgriAdapterizer<IAgriStat> statRegistry;
    private final IAgriAdapterizer<IAgriStatCalculator> statCalculatorRegistry;
    private final IAgriMutationEngine mutationEngine;
    private final IAgriAdapterizer<AgriSeed> seedRegistry;
    private final IAgriAdapterizer<IAgriFertilizer> fertilizerRegistry;

    public AgriApiConnector() {
        this.plantRegistry = new AgriRegistry<>();
        this.mutationRegistry = new AgriMutationRegistry();
        this.soilRegistry = new AgriSoilRegistry();
        this.statRegistry = new AgriAdapterizer<>();
        this.statCalculatorRegistry = new AgriAdapterizer<>();
        this.mutationEngine = new AgriMutationEngine();
        this.seedRegistry = new AgriAdapterizer<>();
        this.fertilizerRegistry = new AgriAdapterizer<>();
    }

    @Override
    public AgriApiState getState() {
        return AgriApiState.VALID;
    }

    @Override
    public IAgriRegistry<IAgriPlant> connectPlantRegistry() {
        return this.plantRegistry;
    }

    @Override
    public IAgriMutationRegistry connectMutationRegistry() {
        return this.mutationRegistry;
    }

    @Override
    public IAgriSoilRegistry connectSoilRegistry() {
        return this.soilRegistry;
    }

    @Override
    public IAgriAdapterizer<IAgriStat> connectStatRegistry() {
        return this.statRegistry;
    }

    @Override
    public IAgriAdapterizer<IAgriStatCalculator> connectStatCalculatorRegistry() {
        return this.statCalculatorRegistry;
    }

    @Override
    public IAgriMutationEngine connectMutationEngine() {
        return this.mutationEngine;
    }

    @Override
    public IAgriAdapterizer<AgriSeed> connectSeedRegistry() {
        return this.seedRegistry;
    }

    @Override
    public IAgriAdapterizer<IAgriFertilizer> connectFertilizerRegistry() {
        return this.fertilizerRegistry;
    }
    
}
