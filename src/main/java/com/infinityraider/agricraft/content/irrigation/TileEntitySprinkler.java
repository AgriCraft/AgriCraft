package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.render.blocks.TileEntitySprinklerRenderer;
import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;
import com.infinityraider.infinitylib.block.tile.TileEntityDynamicTexture;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntitySprinkler extends TileEntityDynamicTexture implements ITickableTileEntity {
    // One full rotation every 2 seconds
    private static final int ROTATION_SPEED = 360/(20 * 2);
    private TileEntityIrrigationChannel channel;

    private int angle;
    private int prevAngle;

    public TileEntitySprinkler() {
        super(AgriCraft.instance.getModTileRegistry().sprinkler);
    }

    public boolean isActive() {
        // TODO: sprinkle logic
        return true;
    }

    @Override
    public void tick() {
        if (this.getWorld() == null) {
            return;
        }
        this.prevAngle = angle;
        if (this.isActive()) {
            this.angle = (this.angle + ROTATION_SPEED) % 360;
            if (!this.getWorld().isRemote()) {
                // TODO: sprinkle logic
            }
        }
    }

    public int getAngle() {
        return this.angle;
    }

    public float getAngle(float partialTick) {
        return MathHelper.lerp(partialTick, this.prevAngle, this.getAngle());
    }

    @Nullable
    public TileEntityIrrigationChannel getChannel() {
        if(this.channel == null) {
            if(this.getWorld() == null) {
                return null;
            }
            TileEntity tile = this.getWorld().getTileEntity(this.getPos().up());
            if(tile instanceof TileEntityIrrigationChannel) {
                this.channel = (TileEntityIrrigationChannel) tile;
            }
        }
        return this.channel;
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

    private static class RenderFactory implements InfinityTileEntityType.IRenderFactory<TileEntitySprinkler> {
        @Nullable
        @OnlyIn(Dist.CLIENT)
        public TileEntitySprinklerRenderer createRenderer() {
            return new TileEntitySprinklerRenderer();
        }
    }
}
