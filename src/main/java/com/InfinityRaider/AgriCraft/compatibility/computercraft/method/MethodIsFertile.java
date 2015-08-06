package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodIsFertile extends MethodCropBase {
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
