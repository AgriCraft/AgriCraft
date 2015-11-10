package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodHasWeeds extends MethodBaseCrop {
    public MethodHasWeeds() {
        super("hasWeeds");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {crop.hasWeed()};
    }
}
