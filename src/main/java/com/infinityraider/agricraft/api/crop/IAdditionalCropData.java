package com.infinityraider.agricraft.api.crop;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Interface for use in storing additional data in a crop.
 *
 */
public interface IAdditionalCropData {

    /**
     * Called when the TileEntity with this data is writing to NBT
     *
     * @return a tag holding all needed information
     */
    NBTTagCompound writeToNBT();

}
