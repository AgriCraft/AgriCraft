package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodGetPlant extends MethodCropBase {
    protected MethodGetPlant() {
        super("getPlant");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.getSeedStack().getDisplayName()};
    }

    @Override
    protected boolean requiresJournal() {
        return false;
    }
}
