package com.infinityraider.agricraft.compat.computercraft.method;

import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;

public class MethodGetBaseBlockType extends MethodBaseGrowthReq {
    public MethodGetBaseBlockType() {
        super("getBaseBlockType");
    }

    @Override
    protected Object[] onMethodCalled(IAgriPlant plant) {
        if(plant==null) {
            return null;
        }
        return new Object[] {plant.getGrowthRequirement().getRequiredType().name()};
    }
}
