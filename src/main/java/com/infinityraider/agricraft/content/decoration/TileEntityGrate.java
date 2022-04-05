package com.infinityraider.agricraft.content.decoration;

import com.infinityraider.agricraft.content.AgriTileRegistry;
import com.infinityraider.infinitylib.block.tile.TileEntityDynamicTexture;
import net.minecraft.block.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class TileEntityGrate extends TileEntityDynamicTexture {
    public TileEntityGrate(BlockPos pos, BlockState state) {
        super(AgriTileRegistry.GRATE, pos, state);
    }

    @Override
    protected void writeTileNBT(@Nonnull CompoundNBT tag) {
        // NOOP
    }

    @Override
    protected void readTileNBT(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {
        // NOOP
    }
}
