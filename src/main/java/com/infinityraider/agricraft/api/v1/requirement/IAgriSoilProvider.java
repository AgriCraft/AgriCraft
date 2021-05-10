package com.infinityraider.agricraft.api.v1.requirement;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Interface to provide soils contained within blocks or tile entities.
 * Register instances in IAgriSoilRegistry.registerSoilProvider()
 */
@FunctionalInterface
public interface IAgriSoilProvider {
    @Nonnull
    Optional<IAgriSoil> getSoil(IBlockReader world, BlockPos pos, BlockState state);
}
