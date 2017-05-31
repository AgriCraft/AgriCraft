/*
 */
package com.infinityraider.agricraft.api.util;

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
 * A fake API connector, for when the API was not found.
 */
public final class FakeApiConnector implements IAgriApiConnector {
    
    public static final FakeApiConnector INSTANCE = new FakeApiConnector();

    private FakeApiConnector() {
        // Nothing to do here but prevent instantiation.
    }

    @Override
    public Optional<IAgriRegistry<IAgriPlant>> connectPlantRegistry() {
        return Optional.empty();
    }

    @Override
    public Optional<IAgriMutationRegistry> connectMutationRegistry() {
        return Optional.empty();
    }

    @Override
    public Optional<IAgriSoilRegistry> connectSoilRegistry() {
        return Optional.empty();
    }

    @Override
    public Optional<IAgriAdapterizer<IAgriStat>> connectStatRegistry() {
        return Optional.empty();
    }

    @Override
    public Optional<IAgriAdapterizer<IAgriStatCalculator>> connectStatCalculatorRegistry() {
        return Optional.empty();
    }

    @Override
    public Optional<IAgriMutationEngine> connectMutationEngine() {
        return Optional.empty();
    }

    @Override
    public Optional<IAgriAdapterizer<AgriSeed>> connectSeedRegistry() {
        return Optional.empty();
    }

    @Override
    public Optional<IAgriAdapterizer<IAgriFertilizer>> connectFertilizerRegistry() {
        return Optional.empty();
    }

}
