package com.infinityraider.agricraft.tiles.peripheral.method;

import com.infinityraider.agricraft.tiles.TileEntityCrop;

public class MethodIsMature extends MethodBaseCrop {
	
    public MethodIsMature() {
        super("isMature");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.isMature()};
    }

}
