package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.api.plant.IAgriPlant;

public class MethodGetNeededSoil extends MethodBaseGrowthReq {
    public MethodGetNeededSoil() {
        super("getNeededSoil");
    }

    @Override
    protected Object[] onMethodCalled(IAgriPlant plant) {
        return new Object[]{
            plant.getGrowthRequirement().getSoils().stream()
                .findFirst()
                .map(s -> s.getName())
                .orElse("null")
        };
    }
}
