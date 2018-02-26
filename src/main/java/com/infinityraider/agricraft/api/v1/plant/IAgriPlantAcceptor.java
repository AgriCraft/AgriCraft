/*
 */
package com.infinityraider.agricraft.api.v1.plant;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    boolean acceptsPlant(@Nullable IAgriPlant plant);

    /**
     * Sets the AgriPlant associated with this instance. Should always return the same result as
     * acceptsPlant() if the plant is invalid.
     *
     * @param plant the plant to associate with this instance.
     * @return if the plant was successfully associated with the instance.
     */
    boolean setPlant(@Nullable IAgriPlant plant);

    /**
     * Removes the AgriPlant associated with this instance.
     *
     * @return the removed plant, or the empty optional if no plant was removed.
     */
    @Nonnull
    Optional<IAgriPlant> removePlant();

}
