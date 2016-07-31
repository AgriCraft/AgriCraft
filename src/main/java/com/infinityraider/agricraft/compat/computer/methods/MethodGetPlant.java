package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.blocks.tiles.TileEntityCrop;

public class MethodGetPlant extends MethodBaseCrop {
    public MethodGetPlant() {
        super("getPlant");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.getPlant().getSeedName()};
    }
}
