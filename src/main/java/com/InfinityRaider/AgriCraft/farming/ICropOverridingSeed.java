package com.InfinityRaider.AgriCraft.farming;

import com.InfinityRaider.AgriCraft.farming.CropOverride;

/** Implement this interface in your seed class if you want custom behaviour in crops*/
public interface ICropOverridingSeed {
    public CropOverride getOverride();
}
