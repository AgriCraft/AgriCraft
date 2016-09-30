package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.api.plant.IAgriPlant;

public class MethodGetBrightnessRange extends MethodBaseGrowthReq {
    public MethodGetBrightnessRange() {
        super("getBrightnessRange");
    }

    @Override
    protected Object[] onMethodCalled(IAgriPlant plant) {
        return new Object[] {
            plant.getGrowthRequirement().getMinLight(),
            plant.getGrowthRequirement().getMaxLight()
        };
    }
}
