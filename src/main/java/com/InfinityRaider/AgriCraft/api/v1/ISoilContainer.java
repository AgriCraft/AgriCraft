package com.InfinityRaider.AgriCraft.api.v1;

import net.minecraft.block.Block;
import net.minecraft.world.World;

/**
 *  implement in block class, can be used for flower pots for example
 */
public interface ISoilContainer {
    /** returns the block contained within this container */
    public Block getSoil(World world, int x, int y, int z);

    public int getSoilMeta(World world, int x, int y, int z);
}
