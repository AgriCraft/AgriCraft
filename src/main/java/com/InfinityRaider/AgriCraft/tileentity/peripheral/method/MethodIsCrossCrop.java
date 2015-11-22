package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodIsCrossCrop extends MethodBaseCrop {
    public MethodIsCrossCrop() {
        super("isCrossCrop");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.isCrossCrop()};
    }

    @Override
    protected boolean requiresJournal() {
        return false;
    }
}
