package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodHasPlant extends MethodBaseCrop {
    public MethodHasPlant() {
        super("hasPlant");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.hasPlant()};
    }
}
