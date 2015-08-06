package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import com.InfinityRaider.AgriCraft.api.v1.RequirementType;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodNeedsBaseBlock extends MethodCropBase {
    public MethodNeedsBaseBlock() {
        super("needsBaseBlock");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.hasPlant() && GrowthRequirementHandler.getGrowthRequirement(crop.getPlant()).getRequiredType()!= RequirementType.NONE};
    }

    @Override
    protected boolean requiresJournal() {
        return true;
    }
}
