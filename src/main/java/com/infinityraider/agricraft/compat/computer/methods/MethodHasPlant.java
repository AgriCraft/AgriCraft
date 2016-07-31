package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.blocks.tiles.TileEntityCrop;

public class MethodHasPlant extends MethodBaseCrop {
    public MethodHasPlant() {
        super("hasPlant");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.hasPlant()};
    }
}
