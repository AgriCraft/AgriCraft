package com.infinityraider.agricraft.tileentity.peripheral.method;

import com.infinityraider.agricraft.tileentity.TileEntityCrop;

public class MethodIsMature extends MethodBaseCrop {
	
    public MethodIsMature() {
        super("isMature");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.isMature()};
    }

}
