package com.infinityraider.agricraft.compat.computercraft.method;

import com.infinityraider.agricraft.api.v3.IAgriCraftPlant;

public class MethodGetBaseBlockType extends MethodBaseGrowthReq {
    public MethodGetBaseBlockType() {
        super("getBaseBlockType");
    }

    @Override
    protected Object[] onMethodCalled(IAgriCraftPlant plant) {
        if(plant==null) {
            return null;
        }
        return new Object[] {plant.getGrowthRequirement().getRequiredType().name()};
    }
}
