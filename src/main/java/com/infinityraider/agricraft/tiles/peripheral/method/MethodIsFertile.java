package com.infinityraider.agricraft.tiles.peripheral.method;

import com.infinityraider.agricraft.tiles.TileEntityCrop;

public class MethodIsFertile extends MethodBaseCrop {
	
    public MethodIsFertile() {
        super("isFertile");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.isFertile()};
    }

}
