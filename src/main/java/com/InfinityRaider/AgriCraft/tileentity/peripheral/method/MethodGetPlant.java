package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodGetPlant extends MethodBaseCrop {
    public MethodGetPlant() {
        super("getPlant");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.getSeedStack().getDisplayName()};
    }
}
