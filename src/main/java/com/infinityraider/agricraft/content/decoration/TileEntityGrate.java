package com.infinityraider.agricraft.content.decoration;

import com.infinityraider.agricraft.content.AgriTileRegistry;
import com.infinityraider.infinitylib.block.tile.TileEntityDynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class TileEntityGrate extends TileEntityDynamicTexture {
    public TileEntityGrate(BlockPos pos, BlockState state) {
        super(AgriTileRegistry.getInstance().grate.get(), pos, state);
    }

    @Override
    protected void writeTileNBT(@Nonnull CompoundTag tag) {
        // NOOP
    }

    @Override
    protected void readTileNBT(@Nonnull CompoundTag tag) {
        // NOOP
    }
}
