package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodNeedsBaseBlock extends MethodCropBase {
    protected MethodNeedsBaseBlock(String name) {
        super(name);
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[0];
    }

    @Override
    protected boolean requiresJournal() {
        return true;
    }
}
