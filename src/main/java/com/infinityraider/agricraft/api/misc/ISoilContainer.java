package com.infinityraider.agricraft.api.misc;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Implement in block class, can be used for flower pots for example.
 * 
 * Candidate for replacement with an IAgriAdapter.
 */
@Deprecated
public interface ISoilContainer {

	/**
	 * returns the block contained within this container
	 */
	Block getSoil(IBlockAccess world, BlockPos pos);

	int getSoilMeta(IBlockAccess world, BlockPos pos);
}
