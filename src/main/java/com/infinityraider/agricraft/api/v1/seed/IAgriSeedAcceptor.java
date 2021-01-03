package com.infinityraider.agricraft.api.v1.seed;

import javax.annotation.Nonnull;

public interface IAgriSeedAcceptor {

    /**
     * Determines if a seed is valid for this specific instance.
     *
     * @param seed the seed to validate for the instance.
     * @return if the seed is valid for the instance.
     */
    boolean acceptsSeed(@Nonnull AgriSeed seed);

    /**
     * Sets the seed associated with this instance.
     *
     * @param seed the seed to associate with this instance.
     * @return true if successful, or false if a seed was present already.
     */
    boolean setSeed(@Nonnull AgriSeed seed);


    /**
     * Removes the seed associated with this instance.
     *
     * @return true if the seed has been removed
     */
    boolean removeSeed();

}
