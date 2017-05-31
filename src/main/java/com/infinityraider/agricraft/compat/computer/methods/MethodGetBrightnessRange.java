package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import java.util.Optional;

public class MethodGetBrightnessRange extends MethodBaseGrowthReq {

    public MethodGetBrightnessRange() {
        super("getBrightnessRange");
    }

    @Override
    protected Object[] onMethodCalled(Optional<IAgriPlant> plant) {
        return plant
                .map(IAgriPlant::getGrowthRequirement)
                .map(g -> new Object[]{g.getMinLight(), g.getMaxLight()})
                .orElse(null);
    }
}
