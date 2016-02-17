package com.infinityraider.agricraft.tileentity.peripheral.method;

import com.infinityraider.agricraft.tileentity.TileEntityCrop;

public class MethodHasWeeds extends MethodBaseCrop {
    public MethodHasWeeds() {
        super("hasWeeds");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.hasWeed()};
    }
}
