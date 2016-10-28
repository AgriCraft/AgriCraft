package com.infinityraider.agricraft.apiimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import net.minecraft.util.Tuple;

import com.infinityraider.agricraft.api.mutation.IAgriCrossStrategy;
import com.infinityraider.agricraft.api.mutation.IAgriMutationEngine;
import com.infinityraider.agricraft.farming.mutation.MutateStrategy;
import com.infinityraider.agricraft.farming.mutation.SpreadStrategy;

/**
 * This class decides whether a plant is spreading or mutating and also
 * calculates the new stats (growth, gain, strength) of the new plant based on
 * the 4 neighbours.
 */
public final class MutationEngine implements IAgriMutationEngine {

    private static final MutationEngine INSTANCE = new MutationEngine();

    private final List<Tuple<Double, IAgriCrossStrategy>> strategies = new ArrayList<>();

    private double sigma = 0;

    private MutationEngine() {
        registerStrategy(new MutateStrategy());
        registerStrategy(new SpreadStrategy());
    }

    public static MutationEngine getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean registerStrategy(IAgriCrossStrategy strategy) {
        if (strategy.getRollChance() >= 1 || strategy.getRollChance() <= 0) {
            throw new IndexOutOfBoundsException("Invalid roll chance!");
        } else if (!hasStrategy(strategy)) {
            this.sigma += strategy.getRollChance();
            this.strategies.add(new Tuple<>(sigma, strategy));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean hasStrategy(IAgriCrossStrategy strategy) {
        return this.strategies.stream().anyMatch(t -> t.getSecond().equals(strategy));
    }

    @Override
    public List<IAgriCrossStrategy> getStrategies() {
        return this.strategies.stream()
                .map(t -> t.getSecond())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<IAgriCrossStrategy> rollStrategy(Random rand) {
        final double value = rand.nextDouble() * sigma;
        return this.strategies.stream()
                // Value looks very important here... lol.
                .filter(t -> value <= t.getFirst())
                .map(t -> t.getSecond())
                .findFirst();
    }

}
