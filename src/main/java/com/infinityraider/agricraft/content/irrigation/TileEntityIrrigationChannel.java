package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.AgriTileRegistry;
import com.infinityraider.agricraft.render.blocks.TileEntityIrrigationChannelRenderer;
import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;
import com.infinityraider.infinitylib.reference.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityIrrigationChannel extends TileEntityIrrigationComponent {
    private static final float MIN_Y = 6*Constants.UNIT;
    private static final float MAX_Y = 10*Constants.UNIT;

    private static final int HEIGHT_INTERVALS = 6;
    private static final float CONTENT_DELTA_FRACTION = 0.10F;

    private ValveState state = ValveState.NONE;
    private int counter = 0;

    public TileEntityIrrigationChannel(BlockPos pos, BlockState state) {
        super(AgriTileRegistry.IRRIGATION_CHANNEL, pos, state, AgriCraft.instance.getConfig().channelCapacity(), MIN_Y, MAX_Y);
    }

    public static RenderFactory createRenderFactory() {
        return new RenderFactory();
    }

    public boolean hasHandWheel() {
        return ((BlockIrrigationChannelAbstract) this.getBlockState().getBlock()).hasHandWheel();
    }

    public boolean hasValve() {
        return BlockIrrigationChannelAbstract.VALVE.fetch(this.getBlockState()).hasValve();
    }

    public boolean isOpen() {
        return BlockIrrigationChannelAbstract.VALVE.fetch(this.getBlockState()).canTransfer();
    }

    public boolean isClosed() {
        return BlockIrrigationChannelAbstract.VALVE.fetch(this.getBlockState()) == BlockIrrigationChannelAbstract.Valve.CLOSED;
    }

    public void setValveState(ValveState state) {
        this.state = state;
        this.counter = state.getDuration();
    }

    public ValveState getValveState() {
        return this.state;
    }

    public float getValveAnimationProgress(float partialTicks) {
        return this.getValveState().getAnimationProgress(this.counter, partialTicks);
    }

    @Override
    protected void tickComponent() {
        if(this.getWorld() != null && this.getWorld().isRemote()) {
            if (this.counter > 0) {
                this.counter -= 1;
                if (this.counter <= 0) {
                    this.state = state.getResult();
                }
            }
        }
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

    public enum ValveState {
        NONE(0),
        OPEN(1),
        CLOSED(0),
        OPENING(CLOSED, OPEN, 20),
        CLOSING(OPEN, CLOSED, 20);

        private final float target;
        private final ValveState from;
        private final ValveState to;
        private final int duration;

        ValveState(float target) {
            this.target = target;
            this.from = this;
            this.to = this;
            this.duration = -1;
        }

        ValveState(ValveState from, ValveState to, int duration) {
            this.from = from;
            this.to = to;
            this.duration = duration;
            this.target = this.getResult().target;
        }

        public float getAnimationProgress(int counter, float partialTick) {
            if(this.hasAnimation()) {
                // to and from are inverted as the counter is counting down
                return MathHelper.lerp(Math.min(1.0F, (counter + partialTick)/this.getDuration()), this.to.target, this.from.target);
            }
            return this.target;
        }

        public ValveState getResult() {
            return this.to;
        }

        public boolean hasAnimation() {
            return this.getDuration() > 0;
        }

        public int getDuration() {
            return this.duration;
        }
    }

    private static class RenderFactory implements InfinityTileEntityType.IRenderFactory<TileEntityIrrigationChannel> {
        @Nullable
        @OnlyIn(Dist.CLIENT)
        public TileEntityIrrigationChannelRenderer createRenderer() {
            return new TileEntityIrrigationChannelRenderer();
        }
    }
}
