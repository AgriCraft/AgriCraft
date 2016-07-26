package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.blocks.tiles.TileEntityCrop;

public class MethodHasWeeds extends MethodBaseCrop {
    public MethodHasWeeds() {
        super("hasWeeds");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.canWeed()};
    }
}
