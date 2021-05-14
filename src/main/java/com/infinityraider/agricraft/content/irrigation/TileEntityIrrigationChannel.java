package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.render.blocks.TileEntityIrrigationChannelRenderer;
import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;
import com.infinityraider.infinitylib.reference.Constants;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityIrrigationChannel extends TileEntityIrrigationComponent {
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

    public static RenderFactory createRenderFactory() {
        return new RenderFactory();
    }

    private static class RenderFactory implements InfinityTileEntityType.IRenderFactory<TileEntityIrrigationChannel> {
        @Nullable
        @OnlyIn(Dist.CLIENT)
        public TileEntityIrrigationChannelRenderer createRenderer() {
            return new TileEntityIrrigationChannelRenderer();
        }
    }
}
