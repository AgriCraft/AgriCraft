package com.InfinityRaider.AgriCraft.utility.multiblock;

import net.minecraft.world.World;

public abstract class MultiBlockLogic {
    /**
     * The main component of the multiblock storing all the data
     */
    protected IMultiBlockComponent rootComponent;

    public MultiBlockLogic(IMultiBlockComponent root) {
        rootComponent = root;
    }

    /**
     * Checks if this component is the root for this multiblock
     * @param component the component to be checked
     * @return if the argument is indeed the root component
     */
    public boolean isRootComponent(IMultiBlockComponent component) {
        return component == rootComponent;
    }

    /** Returns the root component for this multiblock */
    public IMultiBlockComponent getRootComponent() {
        return rootComponent;
    }

    /**
     * Checks if this component is a valid component for this multiblock
     * @param component the component to be checked
     * @return if the component is compatible with this multiblock
     */
    public boolean isValidComponent(IMultiBlockComponent component) {
        return rootComponent.isValidComponent(component);
    }

    /**
     * @return the number of blocks in this multiblock
     */
    public abstract int getMultiBlockCount();

    /**
     * Checks if the block at the given coordinates is part of this multiblock
     * @param world the world object
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return if the block is part of the multiblock
     */
    public abstract boolean isPartOfMultiBlock(World world, int x, int y, int z);

    /**
     * Checks if the multiblock is valid
     * @return true if the multiblock is valid and can be formed
     */
    public abstract boolean checkMultiBlock();

    /**
     * Performs the needed operations when the multiblock is created
     * Assumes the sizes have been set correctly and all blocks in the
     */
    public abstract void createMultiBLock();
}
