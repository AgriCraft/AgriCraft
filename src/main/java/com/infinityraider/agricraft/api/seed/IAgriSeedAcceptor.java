/*
 */
package com.infinityraider.agricraft.api.seed;

/**
 *
 *
 */
public interface IAgriSeedAcceptor {

    /**
     * Determines if a seed is valid for this specific instance.
     *
     * @param seed the seed to validate for the instance.
     * @return if the seed is valid for the instance.
     */
    boolean acceptsSeed(AgriSeed seed);

    /**
     * Sets the seed associated with this instance.
     *
     * @param seed the seed to associate with this instance.
     * @return the seed priorly associated with this instance.
     */
    boolean setSeed(AgriSeed seed);

}
