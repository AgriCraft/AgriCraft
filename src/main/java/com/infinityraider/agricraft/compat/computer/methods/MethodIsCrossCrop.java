package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.blocks.tiles.TileEntityCrop;

public class MethodIsCrossCrop extends MethodBaseCrop {

    public MethodIsCrossCrop() {
        super("isCrossCrop");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.isCrossCrop()};
    }

}
