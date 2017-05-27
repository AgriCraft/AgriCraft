package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.tiles.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class MethodGetCurrentSoil extends MethodBaseCrop {

    public MethodGetCurrentSoil() {
        super("getCurrentSoil");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityCrop crop) {
        IBlockState state = crop.getWorld().getBlockState(crop.getPos().add(0, -1, 0));
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);
        return new Object[]{(new ItemStack(block, 1, meta)).getDisplayName()};
    }
}
