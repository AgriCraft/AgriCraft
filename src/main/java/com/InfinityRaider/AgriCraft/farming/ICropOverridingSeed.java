package com.InfinityRaider.AgriCraft.farming;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Implement this interface in your seed class if you want custom behaviour in crops*/
public interface ICropOverridingSeed {
    public CropOverride getOverride(TileEntityCrop crop);

    /** Return true if this has its own custom growth requirement */
    public boolean hasGrowthRequirement();

    /** Returns a custom growth requirement if needed*/
    public GrowthRequirement getGrowthRequirement();

    @SideOnly(Side.CLIENT)
    public int getRenderType();
}
