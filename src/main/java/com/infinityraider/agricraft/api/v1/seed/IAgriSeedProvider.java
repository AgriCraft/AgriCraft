package com.infinityraider.agricraft.api.v1.seed;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import java.util.Optional;

/**
 * A class for objects containing seeds.
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
    Optional<AgriSeed> getSeed();

}
