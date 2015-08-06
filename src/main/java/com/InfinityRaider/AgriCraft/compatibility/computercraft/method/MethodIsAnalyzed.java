package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodIsAnalyzed extends MethodCropBase {
    public MethodIsAnalyzed() {
        super("isAnalyzed");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) throws MethodException {
        return new Object[] {crop.isAnalyzed()};
    }

    @Override
    protected boolean requiresJournal() {
        return false;
    }
}
