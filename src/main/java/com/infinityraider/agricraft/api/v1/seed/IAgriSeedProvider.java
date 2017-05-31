/*
 */
package com.infinityraider.agricraft.api.v1.seed;

import javax.annotation.Nullable;

/**
 * A class for objects containing seeds.
 *
 *
 */
public interface IAgriSeedProvider {

    /**
     * Determines if the object currently has an associated seed.
     *
     * @return if the object has a plant associated with it.
     */
    boolean hasSeed();

    /**
     * Retrieves the seed associated with this instance.
     *
     * @return the seed associated with the instance or null.
     */
    @Nullable
    AgriSeed getSeed();

}
