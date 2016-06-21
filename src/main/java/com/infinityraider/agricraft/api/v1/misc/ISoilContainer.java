package com.infinityraider.agricraft.api.v1.misc;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 *  implement in block class, can be used for flower pots for example
 */
public interface ISoilContainer {
    /** returns the block contained within this container */
    Block getSoil(World world, BlockPos pos);

    int getSoilMeta(World world, BlockPos pos);
}
