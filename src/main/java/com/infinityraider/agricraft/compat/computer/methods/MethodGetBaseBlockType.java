package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.requirement.RequirementType;

public class MethodGetBaseBlockType extends MethodBaseGrowthReq {
    public MethodGetBaseBlockType() {
        super("getBaseBlockType");
    }

    @Override
    protected Object[] onMethodCalled(IAgriPlant plant) {
        if(plant==null) {
            return null;
        }
        return new Object[] {plant.getGrowthRequirement().getRequiredType().orElse(RequirementType.NONE).name()};
    }
}
