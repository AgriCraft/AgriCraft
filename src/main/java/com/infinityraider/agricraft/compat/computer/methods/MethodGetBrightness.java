package com.infinityraider.agricraft.compat.computer.methods;

import net.minecraft.world.EnumSkyBlock;

import com.infinityraider.agricraft.blocks.tiles.TileEntityCrop;

public class MethodGetBrightness extends MethodBaseCrop {
    public MethodGetBrightness() {
        super("getBrightness");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        return new Object[] {Math.max(crop.getWorld().getLightFor(EnumSkyBlock.SKY, crop.getPos()), crop.getWorld().getLightFor(EnumSkyBlock.BLOCK, crop.getPos()))};
    }
}
