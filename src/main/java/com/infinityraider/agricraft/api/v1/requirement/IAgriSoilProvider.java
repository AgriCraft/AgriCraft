package com.infinityraider.agricraft.api.v1.requirement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Interface to provide soils contained within blocks or tile entities.
 * Register instances in IAgriSoilRegistry.registerSoilProvider()
 */
@FunctionalInterface
public interface IAgriSoilProvider {
    @Nonnull
    Optional<IAgriSoil> getSoil(BlockGetter world, BlockPos pos, BlockState state);
}
