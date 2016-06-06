package com.infinityraider.agricraft.tiles.peripheral.method;

import com.infinityraider.agricraft.tiles.TileEntityCrop;

public class MethodHasWeeds extends MethodBaseCrop {
    public MethodHasWeeds() {
        super("hasWeeds");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.hasWeed()};
    }
}
