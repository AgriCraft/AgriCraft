package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodGetBrightnessRange extends MethodCropBase {
    protected MethodGetBrightnessRange() {
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
