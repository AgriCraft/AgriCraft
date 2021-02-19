package com.infinityraider.agricraft.content.decoration;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.infinitylib.block.tile.TileEntityDynamicTexture;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class TileEntityGrate extends TileEntityDynamicTexture {
    public TileEntityGrate() {
        super(AgriCraft.instance.getModTileRegistry().grate);
    }

    @Override
    protected void writeTileNBT(@Nonnull CompoundNBT tag) {}

    @Override
    protected void readTileNBT(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {}
}
