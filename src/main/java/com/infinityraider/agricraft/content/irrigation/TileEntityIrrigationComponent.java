package com.infinityraider.agricraft.content.irrigation;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.network.MessageIrrigationNeighbourUpdate;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.infinitylib.block.tile.TileEntityDynamicTexture;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Objects;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class TileEntityIrrigationComponent extends TileEntityDynamicTexture implements ITickableTileEntity {
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
        super(type);
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
            float y_a = this.getLevel();
            float y_b = component.getLevel();
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
        if(this.getWorld() != null && this.getWorld().getDimensionType().isUltrawarm()) {
            this.drainWater(Math.max(1, (int) (this.getNetherEvaporationRate()*this.getCapacity())), true);
            if(this.getWorld() instanceof ServerWorld) {
                if(this.getWorld().getRandom().nextDouble() <= 0.20) {
                    double x = this.getPos().getX() + 0.5 * (this.getWorld().getRandom().nextDouble() - 0.5);
                    double y = this.getLevel();
                    double z = this.getPos().getZ() + 0.5 * (this.getWorld().getRandom().nextDouble() - 0.5);
                    ((ServerWorld) this.getWorld()).spawnParticle(ParticleTypes.CLOUD, x, y, z, 1,0, 0.15, 0, 1);
                    this.getWorld().playSound(AgriCraft.instance.getClientPlayer(), this.getPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS,
                            0.5F, 2.6F + (this.getWorld().getRandom().nextFloat() - this.getWorld().getRandom().nextFloat()) * 0.8F);
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

    public float getLevel() {
        return this.levelBuffer + this.getPos().getY();
    }

    public float getRenderLevel(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.prevLevel, this.levelBuffer) + this.getPos().getY();
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
        this.levelBuffer = Math.max(this.getMinLevel(), Math.min(this.getMaxLevel(), level)) - this.getPos().getY();
        this.contentBuffer = this.calculateContents(this.levelBuffer);
        this.checkAndSyncIfNeeded();
    }

    private void setLevelBuffer(float level) {
        this.levelBuffer = level;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public float getMinLevel() {
        return this.minLevel + this.getPos().getY();
    }

    public float getMaxLevel() {
        return this.maxLevel + this.getPos().getY();
    }

    public float getSurfaceFactor() {
        return this.getCapacity()/(this.getMaxLevel() - this.getMinLevel());
    }

    protected int calculateContents(float height) {
        return (int) (this.getCapacity()*(height + this.getPos().getY() - this.getMinLevel())/(this.getMaxLevel() - this.getMinLevel()));
    }

    protected float calculateHeight(int contents) {
        return this.getMinLevel() - this.getPos().getY() + (contents + 0.0F)*(this.getMaxLevel() - this.getMinLevel())/(this.getCapacity() + 0.0F);
    }

    protected void onNeighbourUpdate(BlockPos pos) {
        Arrays.stream(Direction.values())
                .filter(dir -> dir.getAxis().isHorizontal())
                .filter(dir -> this.getPos().offset(dir).equals(pos))
                .forEach(this::onNeighbourUpdate);
    }

    public void onNeighbourUpdate(Direction dir) {
        this.neighbours.onNeighbourUpdate(dir);
        if(this.getWorld() != null && !this.getWorld().isRemote()) {
            new MessageIrrigationNeighbourUpdate(this.getPos(), dir).sendToDimension(this.getWorld());
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
    protected final void writeTileNBT(@Nonnull CompoundNBT tag) {
        tag.putInt(AgriNBT.CONTENTS, this.contentBuffer);
        tag.putFloat(AgriNBT.LEVEL, this.levelBuffer);
        this.writeComponentNBT(tag);
    }

    @Override
    protected final void readTileNBT(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {
        this.contentBuffer = tag.getInt(AgriNBT.CONTENTS);
        this.levelBuffer = tag.getFloat(AgriNBT.LEVEL);
        this.readComponentNBT(state, tag);
    }

    // might need this later
    @SuppressWarnings("unused")
    protected void writeComponentNBT(@Nonnull CompoundNBT tag) {}

    // might need this later
    @SuppressWarnings("unused")
    protected void readComponentNBT(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {}

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
                World world = this.getComponent().getWorld();
                if(world != null) {
                    TileEntity tile = world.getTileEntity(this.getComponent().getPos().offset(this.getDirection()));
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
