package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class TileEntityIrrigationChannel extends TileEntityIrrigationComponent {
    public TileEntityIrrigationChannel() {
        super(AgriCraft.instance.getModTileRegistry().irrigation_channel);
    }

    @Override
    protected void writeTileNBT(@Nonnull CompoundNBT tag) {

    }

    @Override
    protected void readTileNBT(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {

    }
}
