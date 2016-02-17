package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class MethodGetGrowthStage extends MethodBaseCrop {
    public MethodGetGrowthStage() {
        super("getGrowthStage");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        IBlockState state = crop.getWorld().getBlockState(crop.getPos());
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);
        double growthStage = (100.00*meta)/7;
        return new Object[] {growthStage};
    }
}
