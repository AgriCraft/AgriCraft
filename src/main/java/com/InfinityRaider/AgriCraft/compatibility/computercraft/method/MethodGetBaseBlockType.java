package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodGetBaseBlockType extends MethodCropBase {
    public MethodGetBaseBlockType() {
        super("getBaseBlockType");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        CropPlant plant = crop.getPlant();
        if(plant==null) {
            return null;
        }
        return new Object[] {GrowthRequirementHandler.getGrowthRequirement(plant).getRequiredType().name()};
    }

    @Override
    protected boolean requiresJournal() {
        return true;
    }
}
