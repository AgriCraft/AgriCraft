package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.tiles.TileEntityCrop;

public class MethodIsMature extends MethodBaseCrop {

    public MethodIsMature() {
        super("isMature");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[]{crop.isMature()};
    }

}
