package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.render.blocks.TileEntityIrrigationChannelRenderer;
import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;
import com.infinityraider.infinitylib.reference.Constants;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityIrrigationChannel extends TileEntityIrrigationComponent {
    private static final double MIN_Y = 6*Constants.UNIT;
    private static final double MAX_Y = 10*Constants.UNIT;

    private static final int HEIGHT_INTERVALS = 6;
    private static final float CONTENT_DELTA_FRACTION = 0.10F;

    public TileEntityIrrigationChannel() {
        super(AgriCraft.instance.getModTileRegistry().irrigation_channel, AgriCraft.instance.getConfig().channelCapacity(), MIN_Y, MAX_Y);
    }

    public static RenderFactory createRenderFactory() {
        return new RenderFactory();
    }

    @Override
    protected boolean canConnect(@Nonnull TileEntityIrrigationComponent other) {
        return other.isSameMaterial(this);
    }

    @Override
    protected boolean canTransfer(@Nonnull TileEntityIrrigationComponent other, @Nonnull Direction dir) {
        return this.canConnect(other) && BlockIrrigationChannelAbstract.VALVE.fetch(this.getBlockState()).canTransfer();
    }

    @Override
    protected int getLevelIntervalCount() {
        return HEIGHT_INTERVALS;
    }

    @Override
    protected float getContentDeltaFraction() {
        return CONTENT_DELTA_FRACTION;
    }

    @Override
    protected String description() {
        return "channel";
    }

    private static class RenderFactory implements InfinityTileEntityType.IRenderFactory<TileEntityIrrigationChannel> {
        @Nullable
        @OnlyIn(Dist.CLIENT)
        public TileEntityIrrigationChannelRenderer createRenderer() {
            return new TileEntityIrrigationChannelRenderer();
        }
    }
}
