package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodHasWeeds extends MethodCropBase {
    public MethodHasWeeds() {
        super("hasWeeds");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.hasWeed()};
    }

    @Override
    protected boolean requiresJournal() {
        return false;
    }
}
