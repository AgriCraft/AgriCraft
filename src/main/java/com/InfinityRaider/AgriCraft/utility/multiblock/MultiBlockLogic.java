package com.InfinityRaider.AgriCraft.utility.multiblock;

import net.minecraft.nbt.NBTTagCompound;
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
     * Reads NBT data from an NBTTagCompound
     * @param tag the NBTTagCompound to read data from
     */
    public abstract void readFromNBT(NBTTagCompound tag);

    /**
     * Writes data to an NBTTagCompound
     * @param tag the NBTTagCompound to write data to
     */
    public abstract void writeToNBT(NBTTagCompound tag);

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
     * Checks if the existing multiblock has to be expanded
     * @return true if it can be integrated into a new multiblock
     */
    public abstract boolean checkMultiBlockOnPlace();

    /**
     * Checks if the existing multiblock has to be expanded
     * @return true if there is a new valid multiblock to be formed
     */
    public abstract boolean checkToUpdateExistingMultiBlock();

    /**
     * Performs the needed operations when the multiblock is created
     * Assumes the sizes have been set correctly and all blocks in the
     */
    public abstract void createMultiBLock();

    /**
     * Performs the needed operation to break down this multiblock and turn each component into an individual block again
     */
    public abstract void breakMultiBlock();
}
