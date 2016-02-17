package com.infinityraider.agricraft.utility.multiblock;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface IMultiBlockManager<T extends IMultiBlockPartData> {
    /**
     * Called when an IMultiBlockComponent with this manager as type is placed in the world
     * @param world the world object
     * @param pos the block position
     * @param component the component placed
     */
    void onBlockPlaced(World world, BlockPos pos, IMultiBlockComponent<? extends IMultiBlockManager<T>, T> component);

    /**
     * Called when an IMultiBlockComponent with this manager as type is broken
     * @param world the world object
     * @param pos the block position
     * @param component the component broken
     */
    void onBlockBroken(World world, BlockPos pos, IMultiBlockComponent<? extends IMultiBlockManager<T>, T> component);

    /**
     * Performs needed operations to create the multiblock structure
     * @param world the world object
     * @param xMin the minimum x position
     * @param yMin the minimum y position
     * @param zMin the minimum z position
     * @param xMax the maximum x position
     * @param yMax the maximum y position
     * @param zMax the maximum z position
     */
    void createMultiBlock(World world, int xMin, int yMin, int zMin, int xMax, int yMax, int zMax);
}
