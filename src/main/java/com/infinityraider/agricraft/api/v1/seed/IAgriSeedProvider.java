package com.infinityraider.agricraft.api.v1.seed;

import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * A class for objects containing seeds.
 */
public interface IAgriSeedProvider {

    /**
     * Determines if the object currently has an associated seed.
     *
     * @return if the object has a plant associated with it.
     */
    boolean hasSeed();

    /**
     * Getter for the IAgriGenome
     * @return optional containing the genome of the seed, or empty if no seed is planted
     */
    @Nonnull
    Optional<IAgriGenome> getGenome();

    /**
     * Retrieves the seed associated with this instance.
     *
     * @return the seed associated with the instance or empty.
     */
    Optional<AgriSeed> getSeed();

}
