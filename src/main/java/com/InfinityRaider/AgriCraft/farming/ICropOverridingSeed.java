package com.InfinityRaider.AgriCraft.farming;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

/** Implement this interface in your seed class if you want custom behaviour in crops*/
public interface ICropOverridingSeed {
    public CropOverride getOverride(TileEntityCrop crop);
}
