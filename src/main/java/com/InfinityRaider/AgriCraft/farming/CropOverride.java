package com.InfinityRaider.AgriCraft.farming;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.util.IIcon;

public abstract class CropOverride {
    private TileEntityCrop crop;

    public final CropOverride setTileEntity(TileEntityCrop crop) {
        this.crop = crop;
        return this;
    }

    public final TileEntityCrop getCrop() {
        return this.crop;
    }

    /** Return false if you want to define custom growing logic */
    public abstract boolean hasDefaultGrowth();

    /** This is called when the crop doesn't follow default growing and should advance one growth stage*/
    public abstract void increaseGrowth();

    /** Return false if you want to define custom harvesting logic (right clicks)*/
    public abstract boolean hasDefaultHarvesting();

    /** This is called when the crop doesn't follow default harvesting and is harvested*/
    public abstract void onHarvest();

    /** Return false if you want to define custom breaking logic (left clicks)*/
    public abstract boolean hasDefaultBreaking();

    /** This is called when the crop doesn't follow default breaking and is broken */
    public abstract void onBreak();

    /** Immune to weeds or not */
    public abstract boolean immuneToWeed();

    /** Return true if this has its own custom growth requirement */
    public abstract boolean hasGrowthRequirement();

    /** Returns a custom growth requirement if needed*/
    public abstract GrowthRequirement getGrowthRequirement();

    /** Returns a relevant icon */
    public abstract IIcon getIcon();
}
