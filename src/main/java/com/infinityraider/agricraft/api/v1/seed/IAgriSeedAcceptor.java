package com.infinityraider.agricraft.api.v1.seed;

import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IAgriSeedAcceptor {

    /**
     * Determines if a seed is valid for this specific instance.
     *
     * @param seed the seed to validate for the instance.
     * @return if the seed is valid for the instance.
     */
    boolean acceptsSeed(@Nonnull AgriSeed seed);

    /**
     * Sets the genome associated with this instance
     *
     * This method is associated semantically with a spawning action.
     * If a plant is planted conventionally, use either of the plantSeed methods instead.
     *
     * @param genome the genome to associate with this instance
     * @return true if successful, or false if a seed was present already.
     */
    boolean setGenome(@Nonnull IAgriGenome genome);

    /**
     * Plants the seed associated with this instance.
     *
     * This method is associated semantically with a planting action.
     * If a plant spawns through other means, use the setGenome method instead.
     *
     * If the Entity responsible for the planting is known, use the Entity sensitive method instead
     *
     * @param seed the seed to associate with this instance.
     * @return true if successful, or false if a seed was present already.
     */
    boolean plantSeed(@Nonnull AgriSeed seed);

    /**
     * Plants the seed associated with this instance.
     *
     * This method is associated semantically with a planting action.
     * If a plant spawns through other means, use the setGenome method instead.
     *
     * This method is sensitive to the entity who planted the seed
     *
     * @param seed the seed to associate with this instance.
     * @param entity the entity who planted the seed (may be null if planted through automation)
     * @return true if successful, or false if a seed was present already.
     */
    boolean plantSeed(@Nonnull AgriSeed seed, @Nullable LivingEntity entity);


    /**
     * Removes the seed associated with this instance.
     *
     * @return true if the seed has been removed
     */
    boolean removeSeed();

}
