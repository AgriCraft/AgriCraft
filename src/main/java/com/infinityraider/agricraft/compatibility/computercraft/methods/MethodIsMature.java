package com.infinityraider.agricraft.compatibility.computercraft.methods;

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
