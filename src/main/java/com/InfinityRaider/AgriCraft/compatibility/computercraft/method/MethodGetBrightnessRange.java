package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodGetBrightnessRange extends MethodCropBase {
    public MethodGetBrightnessRange() {
        super("getBrightnessRange");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        int[] brightnessRange = GrowthRequirementHandler.getGrowthRequirement(crop.getPlant()).getBrightnessRange();
        return new Object[] {brightnessRange[0], brightnessRange[1]};
    }

    @Override
    protected boolean requiresJournal() {
        return false;
    }
}
