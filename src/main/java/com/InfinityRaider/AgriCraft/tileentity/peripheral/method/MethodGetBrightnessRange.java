package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;

public class MethodGetBrightnessRange extends MethodBaseGrowthReq {
    public MethodGetBrightnessRange() {
        super("getBrightnessRange");
    }

    @Override
    protected Object[] onMethodCalled(CropPlant plant) {
        int[] brightnessRange = GrowthRequirementHandler.getGrowthRequirement(plant).getBrightnessRange();
        return new Object[] {brightnessRange[0], brightnessRange[1]};
    }
}
