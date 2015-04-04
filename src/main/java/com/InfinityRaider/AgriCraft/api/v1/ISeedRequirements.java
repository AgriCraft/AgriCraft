package com.InfinityRaider.AgriCraft.api.v1;

import java.util.List;

import net.minecraft.block.Block;

/**
 * This details the requirements of a specific seed. It is an abstraction of
 * AgriCraft's internal requirements and is not necessarily complete.
 *
 */
public interface ISeedRequirements {
	/**
	 * @return True if this seed must be planted in crops.
	 */
	boolean needsCrops();

	/**
	 * @return A list of blocks this seed can to be planted on. This may be empty
	 *         if the seed cannot be planted or null if it is not a seed at all.
	 */
	List<IBlockWithMeta> getSoilBlock();

	/**
	 * @return A list of blocks of which one must be below the base block for the
	 *         seed to be planted. May be null if there is no such requirement.
	 */
	List<IBlockWithMeta> getBelowBlock();

	/**
	 * @return A list of blocks of which one must be near the base block for the
	 *         seed to be planted. May be null if there is no such requirement.
	 */
	List<IBlockWithMeta> getNearBlocks();

	/**
	 * @return True if the base block must be tilled. If this is true,
	 *         getSoilBlock() will return the untilled block. This may not work
	 *         with soils other than vanilla dirt/farmland.
	 */
	boolean needsTilling();
}
