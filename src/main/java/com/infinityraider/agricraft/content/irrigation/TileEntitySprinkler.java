package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.render.blocks.TileEntitySprinklerRenderer;
import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;
import com.infinityraider.infinitylib.block.tile.TileEntityDynamicTexture;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntitySprinkler extends TileEntityDynamicTexture implements ITickableTileEntity {
    private static final int WORK_HEIGHT = 5;
    private static final int WORK_RADIUS = 3;
    private static final int WORK_DIAMETER = 1 + 2*WORK_RADIUS;
    private static final int WORK_AREA = WORK_DIAMETER*WORK_DIAMETER;
    private static final int WORK_COOLDOWN = 20;
    private static final int BUFFER_SIZE = 100;
    private static final int ROTATION_SPEED = 360/(20 * 2); // One full rotation every 2 seconds

    // Irrigation data
    private TileEntityIrrigationChannel channel;
    private final AutoSyncedField<Boolean> active;

    private int buffer;
    private int columnCounter;
    private int remainingWater;
    private int remainingTicks;

    // Animation data
    private int angle;
    private int prevAngle;
    private int particleCounter;

    public TileEntitySprinkler() {
        super(AgriCraft.instance.getModTileRegistry().sprinkler);
        this.active = this.getAutoSyncedFieldBuilder(false).build();
    }

    public boolean isActive() {
        return this.active.get();
    }

    @Override
    public void tick() {
        if (this.getWorld() == null) {
            return;
        }
        if(!this.getWorld().isRemote()) {
            this.pullWater();
            this.runSprinklerLogic();
        }
        this.prevAngle = angle;
        if (this.isActive()) {
            this.angle = (this.angle + ROTATION_SPEED) % 360;
            if (this.getWorld().isRemote()) {
                this.spawnSprinklerParticles();
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

    protected void pullWater() {
        TileEntityIrrigationChannel channel = this.getChannel();
        if(channel == null) {
            return;
        }
        this.buffer += channel.drainWater(Math.max(BUFFER_SIZE - this.buffer, 0), true);
    }

    protected void runSprinklerLogic() {
        // Step 1: Check if we need to refresh (or reset) the water usage variables.
        if (this.remainingTicks <= 0 || this.remainingWater < 0) {
            this.remainingWater = Math.abs(AgriCraft.instance.getConfig().sprinklerWaterConsumption());
            this.remainingTicks = WORK_COOLDOWN;
        }

        // Step 2: Calculate the per-tick water usage rate, then decrease the remainder-tracking variables.
        // Note: This math executes regardless of if actual consumption occurs, so that the
        //       water usage rate refresh still happens every cooldown period.
        // Note: This should be calculated using int/int division, and not with floats.
        final int waterUsageThisTick = this.remainingWater / this.remainingTicks;
        this.remainingWater -= waterUsageThisTick;
        this.remainingTicks -= 1;

        // Step 3: Check if there is enough water to irrigate this tick, and also if this is a change in state.
        final boolean currentActiveness = (this.buffer >= waterUsageThisTick && this.buffer > 0);
        if (currentActiveness != this.isActive()) {
            this.active.set(currentActiveness);
        }

        // Step 4: If we aren't irrigating, stop.
        if (!this.isActive()) {
            return;
        }

        // Step 5: Otherwise, consume the water and continue.
        this.buffer -= waterUsageThisTick;

        // Step 6: if we're within bounds, sprinkle the next column!
        if (this.columnCounter >= 0 && this.columnCounter < WORK_AREA) {
            irrigateCurrentColumn();
        }

        // Step 7: Update the counter.
        this.columnCounter += 1;

        // Step 8: If the counter exceeds both minima, or if it is (incorrectly) negative, reset it.
        if (this.columnCounter >= WORK_AREA
                && this.columnCounter >= AgriCraft.instance.getConfig().sprinkleInterval()
                || this.columnCounter < 0) {
            this.columnCounter = 0;
        }
    }

    /**
     * Convenience method to wrap the coordinate calculations. Also makes runSprinklerLogic() and
     * irrigateColumn() cleaner.
     */
    protected void irrigateCurrentColumn() {
        final int targetX = this.pos.getX() - WORK_RADIUS + (this.columnCounter % WORK_DIAMETER);
        final int targetZ = this.pos.getZ() - WORK_RADIUS + (this.columnCounter / WORK_DIAMETER);
        final int startY = this.pos.getY() - 1;
        final int stopY = Math.max(this.pos.getY() - WORK_HEIGHT, 0); // Avoid the void.
        irrigateColumn(targetX, targetZ, startY, stopY);
    }

    /**
     * This method will search through a vertical column of positions, starting from the top. It
     * will stop searching any lower once it hits anything other than air or plants. Any plant found
     * has an independent chance for a growth tick. That percentage is controlled by
     * AgriCraftConfig. Farmland also ends the search, but it first has its moisture set to max (7)
     * if it isn't already. The lowest position is special: a plant this far away is not helped.
     * Only farmland is currently.
     */
    @SuppressWarnings("deprecation")
    protected void irrigateColumn(final int targetX, final int targetZ, final int highestY, final int lowestY) {
        if(this.getWorld() == null || !(this.getWorld() instanceof ServerWorld)) {
            return;
        }
        for (int targetY = highestY; targetY >= lowestY; targetY -= 1) {
            BlockPos target = new BlockPos(targetX, targetY, targetZ);
            BlockState state = this.getWorld().getBlockState(target);
            Block block = state.getBlock();

            // Option A: Skip empty/air blocks.
            if (block.isAir(state, this.getWorld(), target)) {
                continue;
            }

            // Option B: Give plants a chance to grow, and then continue onward to irrigate the farmland too.
            if ((block instanceof IPlantable || block instanceof IGrowable) && targetY != lowestY) {
                if (this.getRandom().nextDouble() < AgriCraft.instance.getConfig().sprinkleGrowthChance()) {
                    block.randomTick(state, (ServerWorld) this.getWorld(), target, this.getRandom());
                }
                continue;
            }

            // Option C: Dry farmland gets set as moist.
            if (block instanceof FarmlandBlock) {
                if (state.get(FarmlandBlock.MOISTURE) < 7) {
                    this.getWorld().setBlockState(target, state.with(FarmlandBlock.MOISTURE, 7), 2);
                }
                break; // Explicitly expresses the intent to stop.
            }

            // Option D: If it's none of the above, it blocks the sprinkler's irrigation. Stop.
            break;
        }
    }

    protected void spawnSprinklerParticles() {
        if(this.getWorld() == null) {
            return;
        }
        int particleSetting = AgriCraft.instance.proxy().getParticleSetting(); //0 = all, 1 = decreased; 2 = minimal;
        this.particleCounter = (this.particleCounter + 1) % (particleSetting + 1);
        if (this.particleCounter == 0) {
            AgriCraft.instance.proxy().spawnSprinklerParticles(
                    this.getWorld(),
                    this.getPos().getX() + 0.5,
                    this.getPos().getY() + 0.5,
                    this.getPos().getZ() + 0.5,
                    this.getAngle()
            );
        }
    }

    @Override
    protected void writeTileNBT(@Nonnull CompoundNBT tag) {
        tag.putInt(AgriNBT.CONTENTS, this.buffer);
        tag.putInt(AgriNBT.KEY, this.columnCounter);
        tag.putInt(AgriNBT.LEVEL, this.remainingWater);
        tag.putInt(AgriNBT.ENTRIES, this.remainingTicks);
    }

    @Override
    protected void readTileNBT(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {
        this.buffer = tag.getInt(AgriNBT.CONTENTS);
        this.columnCounter = tag.getInt(AgriNBT.KEY);
        this.remainingWater = tag.getInt(AgriNBT.LEVEL);
        this.remainingTicks = tag.getInt(AgriNBT.ENTRIES);
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
