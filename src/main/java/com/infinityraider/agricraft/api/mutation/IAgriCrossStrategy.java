package com.infinityraider.agricraft.api.mutation;

import java.util.Optional;
import java.util.Random;

import javax.annotation.Nonnull;

import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.api.seed.AgriSeed;

/**
 * Base interface for different cross over strategies.
 */
public interface IAgriCrossStrategy {

    double getRollChance();

    @Nonnull
    Optional<AgriSeed> executeStrategy(IAgriCrop crop, Random rand);

}
