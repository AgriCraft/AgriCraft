package com.infinityraider.agricraft.compatibility.computercraft.methods;

import com.infinityraider.agricraft.tileentity.TileEntityCrop;

public class MethodIsCrossCrop extends MethodBaseCrop {
	
    public MethodIsCrossCrop() {
        super("isCrossCrop");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.isCrossCrop()};
    }

}
