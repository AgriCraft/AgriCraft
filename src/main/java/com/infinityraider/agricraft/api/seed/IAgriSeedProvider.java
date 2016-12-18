/*
 */
package com.infinityraider.agricraft.api.seed;

import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.stat.IAgriStat;
import com.infinityraider.agricraft.api.plant.IAgriPlantProvider;
import com.infinityraider.agricraft.api.stat.IAgriStatProvider;
import java.util.Optional;

/**
 * A class for objects containing seeds.
 *
 *
 */
public interface IAgriSeedProvider extends IAgriPlantProvider, IAgriStatProvider {

    /**
     * Determines if the object currently has an associated seed.
     *
     * @return if the object has a plant associated with it.
     */
    default boolean hasSeed() {
        return hasPlant() && hasStat();
    }

    /**
     * Retrieves the seed associated with this instance.
     *
     * @return the seed associated with the instance or null.
     */
    default Optional<AgriSeed> getSeed() {
        if (this.hasPlant() && this.hasStat()) {
            return Optional.of(new AgriSeed(this.getPlant().get(), this.getStat().get()));
        } else {
            return Optional.empty();
        }
    }

}
