package com.infinityraider.agricraft.tileentity.peripheral.method;

import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;

public class MethodGetBrightnessRange extends MethodBaseGrowthReq {
    public MethodGetBrightnessRange() {
        super("getBrightnessRange");
    }

    @Override
    protected Object[] onMethodCalled(IAgriCraftPlant plant) {
        int[] brightnessRange = plant.getGrowthRequirement().getBrightnessRange();
        return new Object[] {brightnessRange[0], brightnessRange[1]};
    }
}
