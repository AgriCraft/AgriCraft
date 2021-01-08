package com.infinityraider.agricraft.api.v1.plant;

import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;

public interface IAgriPlantAcceptor {

    /**
     * Determines if an AgriPlant is valid for this specific instance.
     *
     * @param plant the plant to validate for the instance.
     * @return if the plant is valid for the instance.
     */
    boolean acceptsPlant(@Nonnull IAgriPlant plant);

    /**
     * Sets the AgriPlant associated with this instance. Should always return the same result as
     * acceptsPlant() if the plant is invalid.
     *
     * Use when the plant is "planted" conventionally, e.g. by a player or automation from a seed, or other item
     *
     * Note that this method will not set the genome for the plant
     *
     * @param plant the plant to associate with this instance.
     * @param entity the entity responsible for planting the plant
     * @return if the plant was successfully associated with the instance.
     */
    boolean setPlant(@Nonnull IAgriPlant plant, @Nonnull LivingEntity entity);

    /**
     * Sets the AgriPlant associated with this instance. Should always return the same result as
     * acceptsPlant() if the plant is invalid.
     *
     * Use when the plant "spawns" for some reason, e.g. through mutation, spreading or magic
     *
     * Note that this method will not set the genome for the plant
     *
     * @param plant the plant to associate with this instance.
     * @return if the plant was successfully associated with the instance.
     */
    boolean setPlant(@Nonnull IAgriPlant plant);

    /**
     * Removes the AgriPlant associated with this instance.
     *
     * @return the removed plant, or the empty optional if no plant was removed.
     */
    @Nonnull
    IAgriPlant removePlant();

}
