/*
 */
package com.infinityraider.agricraft.api.v1.plant;

import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * Interface for objects that have plants.
 *
 *
 */
public interface IAgriPlantProvider {

    /**
     * Determines if the object currently has an associated AgriPlant.
     *
     * @return if the object has a plant associated with it.
     */
    boolean hasPlant();

    /**
     * Retrieves the AgriPlant associated with this instance.
     *
     * @return the plant associated with the instance or null.
     */
    @Nonnull
    Optional<IAgriPlant> getPlant();

}
