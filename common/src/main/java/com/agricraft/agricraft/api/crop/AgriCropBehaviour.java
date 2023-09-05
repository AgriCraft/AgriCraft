package com.agricraft.agricraft.api.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public interface AgriCropBehaviour {

	/**
	 * Called when the crop receive a random tick. Default behaviour apply a growth tick.
	 * @param crop the crop receiving the random tick.
	 * @param random a random number generator
	 */
	default void onRandomTick(AgriCrop crop, RandomSource random) {
		this.onApplyGrowthTick(crop, random);
	}

	/**
	 * Called when the crop receive a growth tick (when the crop should grow).
	 */
	void onApplyGrowthTick(AgriCrop crop, RandomSource random);

	/**
	 * Called when the crop is right-clicked with a bonemeal item
	 * @param crop the crop receiving the bonemeal.
	 * @param random a random number generator
	 */
	default void onPerformBonemeal(AgriCrop crop, RandomSource random) {
		this.onApplyGrowthTick(crop, random);
	}

}
