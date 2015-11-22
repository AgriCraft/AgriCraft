package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodGetGrowthStage extends MethodBaseCrop {
    public MethodGetGrowthStage() {
        super("getGrowthStage");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        double growthStage = (100.00*crop.getWorldObj().getBlockMetadata(crop.xCoord, crop.yCoord, crop.zCoord))/7;
        return new Object[] {growthStage};
    }
}
