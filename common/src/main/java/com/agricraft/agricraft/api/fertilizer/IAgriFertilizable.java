package com.agricraft.agricraft.api.fertilizer;

import net.minecraft.util.RandomSource;

public interface IAgriFertilizable {
	/**
	 * Checks if the fertilizer can be used on this specific instance of a crop. It depends on if
	 * this is a cross crop or a regular crop, and the properties of the fertilizer, and what
	 * configs are enabled, and what plant (if any) is here.
	 *
	 * @param fertilizer the fertilizer to be checked
	 * @return true if either a) This is a cross crop (so a cross-over can happen here), and
	 * fertilizer mutations are enabled, and this fertilizer has the ability to trigger mutations (ie
	 * cross-overs). -OR- b) This is an empty regular crop (so a plant (ie weed) can spawn here).
	 * -OR- c) This is a regular crop with a plant that allows fertilizers. false if either a) This
	 * is a cross crop, and the config is disabled or this fertilizer can't trigger mutations. -OR-
	 * b) This is a regular crop, and there is a plant in it, and the plant does not allow
	 * fertilizing.
	 */
	boolean acceptsFertilizer(AgriFertilizer fertilizer);

	/**
	 * Called after the specified fertilizer has been applied to this crop. The effects and results may be randomized.
	 *
	 * @param fertilizer the fertilizer to be applied.
	 * @param rand the random number generator to be used.
	 */
	default void onApplyFertilizer(AgriFertilizer fertilizer, RandomSource rand) { }

	/**
	 * Applies a growth tick
	 */
	void applyGrowthTick();
}
