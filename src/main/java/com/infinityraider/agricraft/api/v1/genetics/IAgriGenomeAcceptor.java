package com.infinityraider.agricraft.api.v1.genetics;

import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IAgriGenomeAcceptor {

    /**
     * Determines if a genome is valid for this specific instance.
     *
     * @param genome the genome to validate for the instance.
     * @return if the genome is valid for the instance.
     */
    boolean acceptsGenome(@Nonnull IAgriGenome genome);

    /**
     * Sets the genome associated with this instance
     *
     * This method is associated semantically with a spawning action.
     * If a plant is planted conventionally, use either of the plantGenome methods instead.
     *
     * @param genome the genome to associate with this instance
     * @return true if successful, or false if a genome was present already.
     */
    boolean spawnGenome(@Nonnull IAgriGenome genome);

    /**
     * Plants the genome associated with this instance.
     *
     * This method is associated semantically with a planting action.
     * If a plant spawns through other means, use the setGenome method instead.
     *
     * If the Entity responsible for the planting is known, use the Entity sensitive method instead
     *
     * @param genome the genome to associate with this instance.
     * @return true if successful, or false if a genome was present already.
     */
    boolean plantGenome(@Nonnull IAgriGenome genome);

    /**
     * Plants the genome associated with this instance.
     *
     * This method is associated semantically with a planting action.
     * If a plant spawns through other means, use the setGenome method instead.
     *
     * This method is sensitive to the entity who planted the genome
     *
     * @param genome the genome to associate with this instance.
     * @param entity the entity who planted the genome (may be null if planted through automation)
     * @return true if successful, or false if a genome was present already.
     */
    boolean plantGenome(@Nonnull IAgriGenome genome, @Nullable LivingEntity entity);


    /**
     * Removes the genome associated with this instance.
     *
     * @return true if the genome has been removed
     */
    boolean removeGenome();

}
