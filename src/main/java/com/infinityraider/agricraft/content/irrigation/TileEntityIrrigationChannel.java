package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import com.infinityraider.infinitylib.reference.Constants;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class TileEntityIrrigationChannel extends TileEntityIrrigationComponent implements IAgriIrrigationNode {
    private static final double MIN_Y = Constants.UNIT*6;
    private static final double MAX_Y = Constants.UNIT*10;

    public TileEntityIrrigationChannel() {
        super(AgriCraft.instance.getModTileRegistry().irrigation_channel, AgriCraft.instance.getConfig().channelCapacity(), MIN_Y, MAX_Y);
    }

    @Override
    protected void writeTileNBT(@Nonnull CompoundNBT tag) {

    }

    @Override
    protected void readTileNBT(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {

    }

    @Override
    public IAgriIrrigationNode getNode() {
        return this;
    }

    @Override
    protected int getSingleCapacity() {
        return AgriCraft.instance.getConfig().channelCapacity();
    }

    @Override
    public boolean isSource() {
        return false;
    }

    @Override
    public boolean isSink() {
        // TODO: return true on this when a sprinkler is attached
        return false;
    }
}
