package com.InfinityRaider.AgriCraft.utility.multiblock;

import net.minecraftforge.common.util.ForgeDirection;

public interface IMultiBlockComponent<M extends IMultiBlockManager<T>, T extends IMultiBlockPartData> {
    /**
     * @return the main component for this multi block structure
     */
    IMultiBlockComponent getMainComponent();

    /**
     * @return The multi-block manager instance for this component type
     */
    M getMultiBlockManager();

    /**
     * @param data sets the multi-block data to this object
     */
    void setMultiBlockPartData(T data);

    /**
     * @return the multi-block data for this component
     */
    T getMultiBlockData();

    /**
     * @param dir the direction
     * @return if there is a neighbour in the same multiblock for a specified direction
     */
    boolean hasNeighbour(ForgeDirection dir);

    /**
     * @param component the component to be checked
     * @return if this can form a multiblock with the specified component
     */
    boolean isValidComponent(IMultiBlockComponent component);

    /**
     * Called right before the multiblock is created
     * Is only called for the component returned by getMainComponent()
     * @param sizeX the x size of the multiblock about to be created
     * @param sizeY the y size of the multiblock about to be created
     * @param sizeZ the z size of the multiblock about to be created
     */
    void preMultiBlockCreation(int sizeX, int sizeY, int sizeZ);

    /**
     * Called right after the multiblock is created
     * Is only called for the component returned by getMainComponent()
     */
    void postMultiBlockCreation();

    /**
     * Called right before the multiblock is broken
     * Is only called for the component returned by getMainComponent()
     */
    void preMultiBlockBreak();

    /**
     * Called right after the multiblock is broken
     * Is  called for every component
     */
    void postMultiBlockBreak();
}
