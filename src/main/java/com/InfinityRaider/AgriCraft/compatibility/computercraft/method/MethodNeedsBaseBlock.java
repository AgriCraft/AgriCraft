package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import com.InfinityRaider.AgriCraft.api.v1.RequirementType;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;

public class MethodNeedsBaseBlock extends MethodBaseGrowthReq {
    public MethodNeedsBaseBlock() {
        super("needsBaseBlock");
    }

    @Override
    protected Object[] onMethodCalled(CropPlant plant) throws MethodException {
        return new Object[] {GrowthRequirementHandler.getGrowthRequirement(plant).getRequiredType()!= RequirementType.NONE};
    }

    @Override
    protected boolean requiresJournal() {
        return true;
    }
}
