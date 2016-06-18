package com.infinityraider.agricraft.compat.computercraft.method;

import com.infinityraider.agricraft.api.v3.requirment.RequirementType;
import com.infinityraider.agricraft.api.v3.core.IAgriPlant;

public class MethodNeedsBaseBlock extends MethodBaseGrowthReq {
	
    public MethodNeedsBaseBlock() {
        super("needsBaseBlock");
    }

    @Override
    protected Object[] onMethodCalled(IAgriPlant plant) throws MethodException {
        return new Object[] {plant.getGrowthRequirement().getRequiredType()!= RequirementType.NONE};
    }

}
