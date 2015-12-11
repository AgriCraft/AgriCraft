package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.api.v1.RequirementType;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;

public class MethodNeedsBaseBlock extends MethodBaseGrowthReq {
    public MethodNeedsBaseBlock() {
        super("needsBaseBlock");
    }

    @Override
    protected Object[] onMethodCalled(CropPlant plant) throws MethodException {
        return new Object[] {plant.getGrowthRequirement().getRequiredType()!= RequirementType.NONE};
    }

    @Override
    protected boolean requiresJournal() {
        return true;
    }
}
