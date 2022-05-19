package com.infinityraider.agricraft.content.irrigation;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.network.MessageIrrigationNeighbourUpdate;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.infinitylib.block.tile.TileEntityDynamicTexture;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Objects;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class TileEntityIrrigationComponent extends TileEntityDynamicTexture implements IFluidTank {
    /* drain 1% each tick, so it takes 100 ticks (5 seconds) to fully drain a componentt */
    public static final float NETHER_DRAIN_FRACTION = 0.01F;

    // contents
    private final int capacity;
    private final float minLevel;
    private final float maxLevel;

    // These fields hold the actual content and level
    private int contentBuffer;
    private float levelBuffer;
    private float prevLevel;
    // These fields are used to sync the content and level to the client when a large difference is detected
    private final AutoSyncedField<Integer> content;
    private final AutoSyncedField<Float> level;

    // neighbour cache
    private final NeighbourCache neighbours;

    public TileEntityIrrigationComponent(BlockEntityType<?> type, BlockPos pos, BlockState state, int capacity, float minLevel, float maxLevel) {
        super(type, pos, state);
        this.capacity = capacity;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.content = this.getAutoSyncedFieldBuilder(0).withCallBack(this::setContentBuffer).build();
        this.level = this.getAutoSyncedFieldBuilder(this.minLevel).withCallBack(this::setLevelBuffer).build();
        this.neighbours = new NeighbourCache(this);
    }

    @Override
    public final void tick() {
        // Update previous level
        this.prevLevel = this.levelBuffer;
        // If this has sufficient fluid to distribute, try to push to neighbours
        if(this.getContent() > 0) {
            // balance
            this.balanceWithNeighbours();
            // nether logic
            this.runNetherLogic();
        }
        // Tick component
        this.tickComponent();
    }

    protected abstract void tickComponent();

    protected void balanceWithNeighbours() {
        this.neighbours.streamNeighbours(true).forEach(component -> {
            float y_a = this.getWaterLevel();
            float y_b = component.getWaterLevel();
            // If the neighbour has a higher fluid level, try push to it
            if (y_a > y_b) {
                // fetch parameters
                float f_a = this.getSurfaceFactor();
                float f_b = component.getSurfaceFactor();
                float y_a1 = this.getMinLevel();
                // calculate transferable volume
                float v = (y_a - Math.max(y_b, y_a1)) * this.getSurfaceFactor();
                // determine transfer volume
                float dv = f_b*v/(f_a + f_b);
                // only transfer if the delta is positive
                if(dv > 0) {
                    int transfer = (int) dv;
                    // don't transfer more than this contains
                    transfer = Math.min(transfer, this.getContent());
                    // don't transfer more than the component can accept
                    transfer = Math.min(transfer, component.getCapacity() - component.getContent());
                    // do the transfer
                    transfer = component.pushWater(transfer, true);
                    this.drainWater(transfer, true);
                }
            }
        });
    }

    protected void runNetherLogic() {
        if(this.getLevel() != null && this.getLevel().dimensionType().ultraWarm()) {
            this.drainWater(Math.max(1, (int) (this.getNetherEvaporationRate()*this.getCapacity())), true);
            if(this.getLevel() instanceof ServerLevel) {
                if(this.getLevel().getRandom().nextDouble() <= 0.20) {
                    double x = this.getBlockPos().getX() + 0.5 * (this.getLevel().getRandom().nextDouble() - 0.5);
                    double y = this.getWaterLevel();
                    double z = this.getBlockPos().getZ() + 0.5 * (this.getLevel().getRandom().nextDouble() - 0.5);
                    ((ServerLevel) this.getLevel()).addParticle(ParticleTypes.CLOUD, x, y, z, 1,0, 0.15);
                    this.getLevel().playSound(AgriCraft.instance.getClientPlayer(), this.getBlockPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS,
                            0.5F, 2.6F + (this.getLevel().getRandom().nextFloat() - this.getLevel().getRandom().nextFloat()) * 0.8F);
                }
            }
        }
    }

    protected float getNetherEvaporationRate() {
        return NETHER_DRAIN_FRACTION;
    }

    public int getContent() {
        return this.contentBuffer;
    }

    public float getWaterLevel() {
        return this.levelBuffer + this.getBlockPos().getY();
    }

    public float getRenderLevel(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevLevel, this.levelBuffer) + this.getBlockPos().getY();
    }

    public int pushWater(int max, boolean execute) {
        int fill = Math.min(this.getCapacity() - this.getContent(), max);
        if(execute) {
            this.setContent(this.getContent() + fill);
        }
        return fill;
    }

    public int drainWater(int max, boolean execute) {
        int drain = Math.min(this.getContent(), max);
        if(execute) {
            this.setContent(this.getContent() - drain);
        }
        return drain;
    }

    protected void setContent(int content) {
        this.contentBuffer = Math.max(0, Math.min(this.getCapacity(), content));
        this.levelBuffer = this.calculateHeight(this.contentBuffer);
        this.checkAndSyncIfNeeded();
    }

    private void setContentBuffer(int contents) {
        this.contentBuffer = contents;
    }

    protected void setLevel(float level) {
        this.levelBuffer = Math.max(this.getMinLevel(), Math.min(this.getMaxLevel(), level)) - this.getBlockPos().getY();
        this.contentBuffer = this.calculateContents(this.levelBuffer);
        this.checkAndSyncIfNeeded();
    }

    private void setLevelBuffer(float level) {
        this.levelBuffer = level;
    }

    @Nonnull
    @Override
    public FluidStack getFluid() {
        return new FluidStack(Fluids.WATER, this.getContent());
    }

    @Override
    public int getFluidAmount() {
        return this.getContent();
    }

    public int getCapacity() {
        return this.capacity;
    }

    public float getMinLevel() {
        return this.minLevel + this.getBlockPos().getY();
    }

    public float getMaxLevel() {
        return this.maxLevel + this.getBlockPos().getY();
    }

    public float getSurfaceFactor() {
        return this.getCapacity()/(this.getMaxLevel() - this.getMinLevel());
    }

    protected int calculateContents(float height) {
        return (int) (this.getCapacity()*(height + this.getBlockPos().getY() - this.getMinLevel())/(this.getMaxLevel() - this.getMinLevel()));
    }

    protected float calculateHeight(int contents) {
        return this.getMinLevel() - this.getBlockPos().getY() + (contents + 0.0F)*(this.getMaxLevel() - this.getMinLevel())/(this.getCapacity() + 0.0F);
    }

    protected void onNeighbourUpdate(BlockPos pos) {
        Arrays.stream(Direction.values())
                .filter(dir -> dir.getAxis().isHorizontal())
                .filter(dir -> this.getBlockPos().relative(dir).equals(pos))
                .forEach(this::onNeighbourUpdate);
    }

    public void onNeighbourUpdate(Direction dir) {
        this.neighbours.onNeighbourUpdate(dir);
        if(this.getLevel() != null && !this.getLevel().isClientSide()) {
            new MessageIrrigationNeighbourUpdate(this.getBlockPos(), dir).sendToDimension(this.getLevel());
        }
    }

    // Syncs to the client if a difference greater than the sync threshold is detected
    protected void checkAndSyncIfNeeded() {
        boolean level = Math.abs(this.levelBuffer - this.level.get()) >= (this.getMaxLevel() - this.getMinLevel())/this.getLevelIntervalCount();
        boolean content = Math.abs(this.contentBuffer - this.content.get()) >= this.getContentDeltaFraction()*this.getCapacity();
        if(content || level) {
            this.content.set(this.contentBuffer);
            this.level.set(this.levelBuffer);
        }
    }

    @Nullable
    public TileEntityIrrigationComponent getNeighbour(Direction direction) {
        return this.neighbours.getNeighbour(direction);
    }

    protected abstract boolean canConnect(TileEntityIrrigationComponent other);

    protected abstract boolean canTransfer(TileEntityIrrigationComponent other, Direction dir);

    protected abstract int getLevelIntervalCount();

    protected abstract float getContentDeltaFraction();

    @Override
    protected final void writeTileNBT(@Nonnull CompoundTag tag) {
        tag.putInt(AgriNBT.CONTENTS, this.contentBuffer);
        tag.putFloat(AgriNBT.LEVEL, this.levelBuffer);
        this.writeComponentNBT(tag);
    }

    @Override
    protected final void readTileNBT(@Nonnull CompoundTag tag) {
        this.contentBuffer = tag.getInt(AgriNBT.CONTENTS);
        this.levelBuffer = tag.getFloat(AgriNBT.LEVEL);
        this.readComponentNBT(tag);
    }

    // might need this later
    @SuppressWarnings("unused")
    protected void writeComponentNBT(@Nonnull CompoundTag tag) {}

    // might need this later
    @SuppressWarnings("unused")
    protected void readComponentNBT(@Nonnull CompoundTag tag) {}

    public String describeNeighbour(Direction direction) {
        TileEntityIrrigationComponent component = this.getNeighbour(direction);
        return component == null ? "none" : component.description();
    }

    protected abstract String description();

    // Utility class to cache neighbours
    private static class NeighbourCache {
        private final TileEntityIrrigationComponent component;
        private final EnumMap<Direction, Neighbour> neighbours;

        public NeighbourCache(TileEntityIrrigationComponent component) {
            this.component = component;
            this.neighbours = Maps.newEnumMap(Direction.class);
            Arrays.stream(Direction.values()).filter(dir -> dir.getAxis().isHorizontal()).forEach(dir ->
                    this.neighbours.put(dir, new Neighbour(this, dir)));
        }

        public TileEntityIrrigationComponent getComponent() {
            return this.component;
        }

        @Nullable
        public TileEntityIrrigationComponent getNeighbour(Direction dir) {
            return this.neighbours.containsKey(dir) ? this.neighbours.get(dir).getNeighbour() : null;
        }

        public void onNeighbourUpdate(Direction dir) {
            if(dir.getAxis().isHorizontal()) {
                this.neighbours.get(dir).clear();
            }
        }

        @SuppressWarnings("unused") // meh
        public Stream<TileEntityIrrigationComponent> streamComponents() {
            return Stream.concat(
                    Stream.of(this.getComponent()),
                    this.streamNeighbours()
            );
        }

        public Stream<TileEntityIrrigationComponent> streamNeighbours() {
            return this.streamNeighbours(false);
        }

        public Stream<TileEntityIrrigationComponent> streamNeighbours(boolean transfer) {
            return this.neighbours.values().stream()
                    .filter(neighbour -> (!transfer) || neighbour.canTransfer())
                    .map(Neighbour::getNeighbour)
                    .filter(Objects::nonNull);
        }

    }

    // Utility class representing a cached neighbour
    private static class Neighbour {
        private final NeighbourCache cache;
        private final Direction dir;

        private TileEntityIrrigationComponent neighbour;
        private boolean needsUpdating;

        public Neighbour(NeighbourCache cache, Direction dir) {
            this.cache = cache;
            this.dir = dir;
            this.needsUpdating = true;
        }

        public TileEntityIrrigationComponent getComponent() {
            return this.cache.getComponent();
        }

        public Direction getDirection() {
            return this.dir;
        }

        public boolean needsUpdate() {
            return this.needsUpdating;
        }

        @Nullable
        public TileEntityIrrigationComponent getNeighbour() {
            return this.updateIfNecessary().neighbour;
        }

        public boolean canTransfer() {
            TileEntityIrrigationComponent neighbour = this.getNeighbour();
            if(neighbour == null) {
                return false;
            }
            return this.getComponent().canTransfer(neighbour, this.getDirection())
                    && neighbour.canTransfer(this.getComponent(), this.getDirection().getOpposite());
        }

        protected Neighbour updateIfNecessary() {
            if(this.needsUpdate()) {
                Level world = this.getComponent().getLevel();
                if(world != null) {
                    BlockEntity tile = world.getBlockEntity(this.getComponent().getBlockPos().relative(this.getDirection()));
                    if(tile instanceof TileEntityIrrigationComponent) {
                        TileEntityIrrigationComponent neighbour = (TileEntityIrrigationComponent) tile;
                        if(this.getComponent().canConnect(neighbour)) {
                            this.neighbour = neighbour;
                        } else {
                            this.neighbour = null;
                        }
                    } else {
                        this.neighbour = null;
                    }
                    this.needsUpdating = false;
                }
            }
            return this;
        }

        public void clear() {
            this.neighbour = null;
            this.needsUpdating = true;
        }
    }
}
