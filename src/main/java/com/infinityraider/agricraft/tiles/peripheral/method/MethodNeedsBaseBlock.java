package com.infinityraider.agricraft.tiles.peripheral.method;

import com.infinityraider.agricraft.api.v1.RequirementType;
import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;

public class MethodNeedsBaseBlock extends MethodBaseGrowthReq {
	
    public MethodNeedsBaseBlock() {
        super("needsBaseBlock");
    }

    @Override
    protected Object[] onMethodCalled(IAgriCraftPlant plant) throws MethodException {
        return new Object[] {plant.getGrowthRequirement().getRequiredType()!= RequirementType.NONE};
    }

}
