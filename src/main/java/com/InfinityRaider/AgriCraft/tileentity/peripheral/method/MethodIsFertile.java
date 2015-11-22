package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodIsFertile extends MethodBaseCrop {
    public MethodIsFertile() {
        super("isFertile");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.isFertile()};
    }

    @Override
    protected boolean requiresJournal() {
        return false;
    }
}
