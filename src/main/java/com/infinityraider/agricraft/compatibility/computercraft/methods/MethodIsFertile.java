package com.infinityraider.agricraft.compatibility.computercraft.methods;

import com.infinityraider.agricraft.tileentity.TileEntityCrop;

public class MethodIsFertile extends MethodBaseCrop {
	
    public MethodIsFertile() {
        super("isFertile");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.isFertile()};
    }

}
