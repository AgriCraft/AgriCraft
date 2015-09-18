package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;

public class MethodGetCurrentSoil extends MethodCropBase {
    public MethodGetCurrentSoil() {
        super("getCurrentSoil");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        Block block = crop.getWorldObj().getBlock(crop.xCoord, crop.yCoord-1, crop.zCoord);
        int meta = crop.getWorldObj().getBlockMetadata(crop.xCoord, crop.yCoord-1, crop.zCoord);
        return new Object[] {(new ItemStack(block, 1, meta)).getDisplayName()};
    }

    @Override
    protected boolean requiresJournal() {
        return false;
    }
}
