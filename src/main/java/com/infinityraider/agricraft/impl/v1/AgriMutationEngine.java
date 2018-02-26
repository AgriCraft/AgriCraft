package com.infinityraider.agricraft.impl.v1;

import com.agricraft.agricore.core.AgriCore;
import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.api.v1.mutation.IAgriCrossStrategy;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutationEngine;
import com.infinityraider.agricraft.farming.mutation.MutateStrategy;
import com.infinityraider.agricraft.farming.mutation.SpreadStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.Tuple;

/**
 * This class decides whether a plant is spreading or mutating and also calculates the new stats
 * (growth, gain, strength) of the new plant based on the 4 neighbours.
 */
public final class AgriMutationEngine implements IAgriMutationEngine {

    @Nonnull
    private final List<Tuple<Double, IAgriCrossStrategy>> strategies;

    private double sigma = 0;

    public AgriMutationEngine() {
        this.strategies = new ArrayList<>();
        this.registerStrategy(new MutateStrategy());
        this.registerStrategy(new SpreadStrategy());
    }

    @Override
    public boolean registerStrategy(IAgriCrossStrategy strategy) {
        // Validate
        Preconditions.checkNotNull(strategy, "Cannot register a null strategy to the mutation engine!");

        // Register
        if (strategy.getRollChance() > 1f || strategy.getRollChance() < 0f) {
            throw new IndexOutOfBoundsException(
                    "Invalid roll chance of " + strategy.getRollChance() + "!\n"
                    + "The roll chance must be in the range 0.0 (inclusive) to 1.0 (exclusive)!"
            );
        } else if (strategy.getRollChance() == 0) {
            AgriCore.getLogger("agricraft").debug("Skipping mutation strategy with zero chance!");
            return false;
        } else if (hasStrategy(strategy)) {
            AgriCore.getLogger("agricraft").debug("Skipping duplicate mutation strategy!");
            return false;
        } else {
            this.sigma += strategy.getRollChance();
            this.strategies.add(new Tuple<>(sigma, strategy));
            return true;
        }
    }

    @Override
    public boolean hasStrategy(@Nullable IAgriCrossStrategy strategy) {
        return this.strategies.stream().anyMatch(t -> t.getSecond().equals(strategy));
    }

    @Override
    @Nonnull
    public List<IAgriCrossStrategy> getStrategies() {
        return this.strategies.stream()
                .map(t -> t.getSecond())
                .collect(Collectors.toList());
    }

    @Override
    @Nonnull
    public Optional<IAgriCrossStrategy> rollStrategy(@Nonnull Random rand) {
        // Validate
        Preconditions.checkNotNull(rand);

        // Roll
        final double value = rand.nextDouble() * sigma;
        return this.strategies.stream()
                // Value looks very important here... lol.
                .filter(t -> value <= t.getFirst())
                .map(t -> t.getSecond())
                .findFirst();
    }

}
