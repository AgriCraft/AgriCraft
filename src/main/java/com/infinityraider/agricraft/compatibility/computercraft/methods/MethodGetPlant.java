package com.infinityraider.agricraft.compatibility.computercraft.methods;

import com.infinityraider.agricraft.tileentity.TileEntityCrop;

public class MethodGetPlant extends MethodBaseCrop {
    public MethodGetPlant() {
        super("getPlant");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.getSeedStack().getDisplayName()};
    }
}
