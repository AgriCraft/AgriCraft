package com.infinityraider.agricraft.tiles.peripheral.method;

import com.infinityraider.agricraft.tiles.TileEntityCrop;

public class MethodGetPlant extends MethodBaseCrop {
    public MethodGetPlant() {
        super("getPlant");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.getSeedStack().getDisplayName()};
    }
}
