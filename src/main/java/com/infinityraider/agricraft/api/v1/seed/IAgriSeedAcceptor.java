/*
 */
package com.infinityraider.agricraft.api.v1.seed;

import javax.annotation.Nullable;

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
    boolean acceptsSeed(@Nullable AgriSeed seed);

    /**
     * Sets the seed associated with this instance. Use null to remove the seed from this instance.
     *
     * @param seed the seed to associate with this instance.
     * @return true if this changed the value, or false if they were equal already.
     */
    boolean setSeed(@Nullable AgriSeed seed);

}
