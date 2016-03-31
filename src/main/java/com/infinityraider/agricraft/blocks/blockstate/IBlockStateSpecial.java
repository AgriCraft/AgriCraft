package com.infinityraider.agricraft.blocks.blockstate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Special block state containing the tile entity and block position of a block
 * @param <T> Tile Entity type
 */
public interface IBlockStateSpecial<T extends TileEntity, S extends IBlockState> extends IBlockState {
    T getTileEntity(IBlockAccess world);

    BlockPos getPos();

    /**
     * Gets the original block state wrapped in this block state
     * @return original state
     */
    S getWrappedState();
}
