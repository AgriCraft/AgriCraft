package com.agricraft.agricraft.api.requirement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Interface to provide soils contained within blocks or tile entities.
 * Register instances in IAgriSoilRegistry.registerSoilProvider()
 */
@FunctionalInterface
public interface IAgriSoilProvider {

	@NotNull
	Optional<IAgriSoil> getSoil(BlockGetter world, BlockPos pos, BlockState state);

}
