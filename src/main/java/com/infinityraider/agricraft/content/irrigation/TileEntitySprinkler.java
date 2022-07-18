package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.AgriParticleRegistry;
import com.infinityraider.agricraft.content.AgriTileRegistry;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.render.blocks.TileEntitySprinklerRenderer;
import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;
import com.infinityraider.infinitylib.block.tile.TileEntityDynamicTexture;
import com.infinityraider.infinitylib.reference.Constants;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntitySprinkler extends TileEntityDynamicTexture {
    private static final int WORK_HEIGHT = 5;
    private static final int WORK_RADIUS = 3;
    private static final int WORK_DIAMETER = 1 + 2*WORK_RADIUS;
    private static final int WORK_AREA = WORK_DIAMETER*WORK_DIAMETER;
    private static final int WORK_COOLDOWN = 20;
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

    public TileEntitySprinkler(BlockPos pos, BlockState state) {
        super(AgriTileRegistry.getInstance().sprinkler.get(), pos, state);
        this.active = this.getAutoSyncedFieldBuilder(false).build();
    }

    public boolean isActive() {
        return this.active.get();
    }

    @Override
    public void tick() {
        if (this.getLevel() == null) {
            return;
        }
        if(!this.getLevel().isClientSide()) {
            this.pullWater();
            this.runSprinklerLogic();
        }
        this.prevAngle = angle;
        if (this.isActive()) {
            this.angle = (this.angle + ROTATION_SPEED) % 360;
            if (this.getLevel().isClientSide()) {
                this.spawnSprinklerParticles();
            }
        }
    }

    public int getAngle() {
        return this.angle;
    }

    public float getAngle(float partialTick) {
        return Mth.lerp(partialTick, this.prevAngle, this.getAngle());
    }

    @Nullable
    public TileEntityIrrigationChannel getChannel() {
        if(this.channel == null) {
            if(this.getLevel() == null) {
                return null;
            }
            BlockEntity tile = this.getLevel().getBlockEntity(this.getBlockPos().above());
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
        this.buffer += channel.drainWater(Math.max(this.getBufferSize() - this.buffer, 0), true);
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
        final int targetX = this.getBlockPos().getX() - WORK_RADIUS + (this.columnCounter % WORK_DIAMETER);
        final int targetZ = this.getBlockPos().getZ() - WORK_RADIUS + (this.columnCounter / WORK_DIAMETER);
        final int startY = this.getBlockPos().getY() - 1;
        final int stopY = Math.max(this.getBlockPos().getY() - WORK_HEIGHT, 0); // Avoid the void.
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
        if(this.getLevel() == null || !(this.getLevel() instanceof ServerLevel)) {
            return;
        }
        for (int targetY = highestY; targetY >= lowestY; targetY -= 1) {
            BlockPos target = new BlockPos(targetX, targetY, targetZ);
            BlockState state = this.getLevel().getBlockState(target);

            // Option A: Skip empty/air blocks.
            if (state.isAir()) {
                continue;
            }

            // Option B: Give plants a chance to grow, and then continue onward to irrigate the farmland too.
            Block block = state.getBlock();
            if ((block instanceof IPlantable || block instanceof BonemealableBlock) && targetY != lowestY) {
                if (this.getRandom().nextDouble() < AgriCraft.instance.getConfig().sprinkleGrowthChance()) {
                    block.randomTick(state, (ServerLevel) this.getLevel(), target, this.getRandom());
                }
                continue;
            }

            // Option C: Dry farmland gets set as moist.
            if (block instanceof FarmBlock) {
                if (state.getValue(FarmBlock.MOISTURE) < 7) {
                    this.getLevel().setBlock(target, state.setValue(FarmBlock.MOISTURE, 7), 2);
                }
                break; // Explicitly expresses the intent to stop.
            }

            // Option D: If it's none of the above, it blocks the sprinkler's irrigation. Stop.
            break;
        }
    }

    protected int getBufferSize() {
        return 2*AgriCraft.instance.getConfig().sprinklerWaterConsumption();
    }

    protected void spawnSprinklerParticles() {
        if (this.getLevel() == null) {
            return;
        }
        if (AgriCraft.instance.getConfig().disableParticles()) {
            return;
        }
        boolean vapour = this.getLevel().dimensionType().ultraWarm();
        int particleSetting = AgriCraft.instance.proxy().getParticleSetting(); //0 = all, 1 = decreased; 2 = minimal;
        particleSetting += vapour ? 6 : 2;
        this.particleCounter = (this.particleCounter + 1) % particleSetting;
        if (this.particleCounter == 0) {
            double x = this.getBlockPos().getX() + 0.5;
            double y = this.getBlockPos().getY() + 0.35;
            double z = this.getBlockPos().getZ() + 0.5;
            for (int i = 0; i < 4; i++) {
                float alpha = -(angle + 90 * i) * ((float) Math.PI) / 180;
                float cosA = Mth.cos(alpha);
                float sinA = Mth.sin(alpha);
                double xOffset = (4 * Constants.UNIT) * cosA;
                double zOffset = (4 * Constants.UNIT) * sinA;
                float radius = 0.35F;
                if (vapour) {
                    ParticleOptions particle = ParticleTypes.CLOUD;
                    this.getLevel().addParticle(particle, x + xOffset, y, z + zOffset, 0.15*cosA, 0.25, 0.15*sinA);
                } else {
                    ParticleOptions particle = AgriParticleRegistry.getInstance().sprinkler.get().createParticleData(Fluids.WATER, 0.2F, 0.7F);
                    for (int j = 0; j <= 4; j++) {
                        float beta = -j * ((float) Math.PI) / (8.0F);
                        this.getLevel().addParticle(particle,
                                x + xOffset * (4 - j) / 4, y, z + zOffset * (4 - j) / 4,
                                radius * cosA, radius * Math.sin(beta), radius * sinA);
                    }
                }
            }
            if(vapour) {
                this.getLevel().playSound(AgriCraft.instance.getClientPlayer(), this.getBlockPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS,
                        0.5F, 2.6F + (this.getLevel().getRandom().nextFloat() - this.getLevel().getRandom().nextFloat()) * 0.8F);
            }
        }
    }

    @Override
    protected void writeTileNBT(@Nonnull CompoundTag tag) {
        tag.putInt(AgriNBT.CONTENTS, this.buffer);
        tag.putInt(AgriNBT.KEY, this.columnCounter);
        tag.putInt(AgriNBT.LEVEL, this.remainingWater);
        tag.putInt(AgriNBT.ENTRIES, this.remainingTicks);
    }

    @Override
    protected void readTileNBT(@Nonnull CompoundTag tag) {
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
