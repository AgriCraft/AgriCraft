package com.infinityraider.agricraft.tiles.peripheral.method;

import com.infinityraider.agricraft.tiles.TileEntityCrop;

public class MethodHasPlant extends MethodBaseCrop {
    public MethodHasPlant() {
        super("hasPlant");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.hasPlant()};
    }
}
