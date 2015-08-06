package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodGetBrightness extends MethodCropBase {
    public MethodGetBrightness() {
        super("getBrightness");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.getWorldObj().getFullBlockLightValue(crop.xCoord, crop.yCoord+1, crop.zCoord)};
    }

    @Override
    protected boolean requiresJournal() {
        return false;
    }
}
