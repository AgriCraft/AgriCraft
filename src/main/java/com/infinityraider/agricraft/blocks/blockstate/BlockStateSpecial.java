package com.infinityraider.agricraft.blocks.blockstate;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockStateSpecial<T extends TileEntity, S extends IBlockState> extends BlockStateContainer.StateImplementation
        implements IBlockStateSpecial<T, S> {

    private final T tile;
    private final BlockPos pos;
    private final S state;

    public BlockStateSpecial(S state, BlockPos pos, T tile) {
        super(state.getBlock(), state.getProperties());
        this.state = state;
        this.tile = tile;
        this.pos = pos;
    }

    @Override
    public T getTileEntity(IBlockAccess world) {
        return tile;
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public S getWrappedState() {
        return this.state;
    }
}
