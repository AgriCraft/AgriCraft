/*
 */
package com.infinityraider.agricraft.api.v1.plant;

import java.util.Optional;

/**
 *
 *
 */
public interface IAgriPlantAcceptor {

    /**
     * Determines if an AgriPlant is valid for this specific instance.
     *
     * @param plant the plant to validate for the instance.
     * @return if the plant is valid for the instance.
     */
    boolean acceptsPlant(IAgriPlant plant);

    /**
     * Sets the AgriPlant associated with this instance. Should always return
     * the same result as acceptsPlant() if the plant is invalid.
     *
     * @param plant the plant to associate with this instance.
     * @return if the plant was successfully associated with the instance.
     */
    boolean setPlant(IAgriPlant plant);

    /**
     * Removes the AgriPlant associated with this instance.
     *
     * @return the removed plant, or the empty optional if no plant was removed.
     */
    Optional<IAgriPlant> removePlant();

}
