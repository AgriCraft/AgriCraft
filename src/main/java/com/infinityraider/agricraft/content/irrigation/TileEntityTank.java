package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class TileEntityTank extends TileEntityIrrigationComponent {
    public TileEntityTank() {
        super(AgriCraft.instance.getModTileRegistry().irrigation_tank);
    }

    @Override
    protected void writeTileNBT(@Nonnull CompoundNBT tag) {

    }

    @Override
    protected void readTileNBT(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {

    }
}
