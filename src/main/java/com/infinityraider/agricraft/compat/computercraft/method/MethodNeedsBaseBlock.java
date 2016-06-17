package com.infinityraider.agricraft.compat.computercraft.method;

import com.infinityraider.agricraft.api.v3.RequirementType;
import com.infinityraider.agricraft.api.v3.IAgriCraftPlant;

public class MethodNeedsBaseBlock extends MethodBaseGrowthReq {
	
    public MethodNeedsBaseBlock() {
        super("needsBaseBlock");
    }

    @Override
    protected Object[] onMethodCalled(IAgriCraftPlant plant) throws MethodException {
        return new Object[] {plant.getGrowthRequirement().getRequiredType()!= RequirementType.NONE};
    }

}
