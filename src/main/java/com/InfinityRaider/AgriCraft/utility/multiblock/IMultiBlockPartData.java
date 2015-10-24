package com.InfinityRaider.AgriCraft.utility.multiblock;

import net.minecraft.nbt.NBTTagCompound;

public interface IMultiBlockPartData {
    /**
     * @return the relative x-position in the multi-block structure
     */
    int posX();

    /**
     * @return the relative y-position in the multi-block structure
     */
    int posY();

    /**
     * @return the relative z-position in the multi-block structure
     */
    int posZ();

    /**
     * @return the x-size of the multi-block structure
     */
    int sizeX();

    /**
     * @return the y-size of the multi-block structure
     */
    int sizeY();

    /**
     * @return the z-size of the multi-block structure
     */
    int sizeZ();

    /**
     * @return the number of blocks in the multi-block structure
     */
    int size();

    /**
     * @param tag NBT tag to write this part's data to
     */
    void writeToNBT(NBTTagCompound tag);

    /**
     * @param tag NBT tag to read this part's data from
     */
    void readFromNBT(NBTTagCompound tag);
}
