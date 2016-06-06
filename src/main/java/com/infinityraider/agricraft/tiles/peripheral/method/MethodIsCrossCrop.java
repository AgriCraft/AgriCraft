package com.infinityraider.agricraft.tiles.peripheral.method;

import com.infinityraider.agricraft.tiles.TileEntityCrop;

public class MethodIsCrossCrop extends MethodBaseCrop {
	
    public MethodIsCrossCrop() {
        super("isCrossCrop");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.isCrossCrop()};
    }

}
