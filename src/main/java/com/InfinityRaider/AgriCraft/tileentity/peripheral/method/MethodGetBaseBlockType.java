package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;

public class MethodGetBaseBlockType extends MethodBaseGrowthReq {
    public MethodGetBaseBlockType() {
        super("getBaseBlockType");
    }

    @Override
    protected Object[] onMethodCalled(CropPlant plant) {
        if(plant==null) {
            return null;
        }
        return new Object[] {GrowthRequirementHandler.getGrowthRequirement(plant).getRequiredType().name()};
    }
}
