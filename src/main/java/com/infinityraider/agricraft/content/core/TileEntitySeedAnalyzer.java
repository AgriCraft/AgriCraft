package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.infinitylib.block.tile.TileEntityBase;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class TileEntitySeedAnalyzer extends TileEntityBase {
    public TileEntitySeedAnalyzer() {
        super(AgriCraft.instance.getModTileRegistry().seed_analyzer);
    }

    @Override
    protected void writeTileNBT(@Nonnull CompoundNBT tag) {

    }

    @Override
    protected void readTileNBT(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {

    }
}
