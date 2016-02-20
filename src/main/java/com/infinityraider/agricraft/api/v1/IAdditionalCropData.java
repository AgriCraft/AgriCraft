package com.infinityraider.agricraft.api.v1;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Use this interface to store additional data to crops
 */
public interface IAdditionalCropData {
    /**
     * Called when the TileEntity with this data is writing to NBT
     * @return a tag holding all needed information
     */
    NBTTagCompound writeToNBT();
}
