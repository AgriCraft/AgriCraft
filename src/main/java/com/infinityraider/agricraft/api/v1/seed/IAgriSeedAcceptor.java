package com.infinityraider.agricraft.api.v1.seed;

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
     * Sets the seed associated with this instance.
     *
     * If the Entity responsible for the planting is known, use the Entity sensitive method instead
     *
     * @param seed the seed to associate with this instance.
     * @return true if successful, or false if a seed was present already.
     */
    boolean setSeed(@Nonnull AgriSeed seed);

    /**
     * Sets the seed associated with this instance.
     *
     * This method is sensitive to the entity who planted the seed
     *
     * @param seed the seed to associate with this instance.
     * @param entity the entity who planted the seed (may be null if planted through automation)
     * @return true if successful, or false if a seed was present already.
     */
    boolean setSeed(@Nonnull AgriSeed seed, @Nullable LivingEntity entity);


    /**
     * Removes the seed associated with this instance.
     *
     * @return true if the seed has been removed
     */
    boolean removeSeed();

}
