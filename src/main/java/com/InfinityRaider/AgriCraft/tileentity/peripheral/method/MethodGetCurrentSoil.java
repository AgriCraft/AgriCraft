package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class MethodGetCurrentSoil extends MethodBaseCrop {
    public MethodGetCurrentSoil() {
        super("getCurrentSoil");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        Block block = crop.getWorldObj().getBlock(crop.xCoord, crop.yCoord-1, crop.zCoord);
        int meta = crop.getWorldObj().getBlockMetadata(crop.xCoord, crop.yCoord-1, crop.zCoord);
        return new Object[] {(new ItemStack(block, 1, meta)).getDisplayName()};
    }
}
