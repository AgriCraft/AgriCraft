package com.infinityraider.agricraft.content.irrigation;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.AgriBlockRegistry;
import com.infinityraider.agricraft.content.AgriTileRegistry;
import com.infinityraider.agricraft.render.blocks.TileEntityIrrigationTankRenderer;
import com.infinityraider.infinitylib.block.tile.InfinityTileEntityType;
import com.infinityraider.infinitylib.reference.Constants;
import com.infinityraider.infinitylib.utility.TileReference;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.BiFunction;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntityIrrigationTank extends TileEntityIrrigationComponent implements IFluidHandler {
    private static final BlockPos DEFAULT = new BlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    private static final BiFunction<LevelAccessor, BlockPos, TileEntityIrrigationTank> TANK_GETTER = (world, pos) -> {
        BlockEntity tile = world.getBlockEntity(pos);
        if(tile instanceof TileEntityIrrigationTank) {
            return (TileEntityIrrigationTank) tile;
        }
        return null;
    };

    private static final float MIN_Y = 2*Constants.UNIT;
    private static final float MAX_Y = 1;

    private static final int HEIGHT_INTERVALS = 16;
    private static final float CONTENT_DELTA_FRACTION = 0.05F;

    private TileReference<TileEntityIrrigationTank> origin;
    private final AutoSyncedField<BlockPos> min;
    private final AutoSyncedField<BlockPos> max;

    private final LazyOptional<IFluidHandler> capability;

    public TileEntityIrrigationTank(BlockPos pos, BlockState state) {
        super(AgriTileRegistry.IRRIGATION_TANK, pos, state, AgriCraft.instance.getConfig().tankCapacity(), MIN_Y, MAX_Y);
        this.min = this.getAutoSyncedFieldBuilder(DEFAULT).build();
        this.max = this.getAutoSyncedFieldBuilder(DEFAULT).build();
        this.capability = LazyOptional.of(() -> this);
    }

    public BlockIrrigationTank getBlock() {
        return AgriBlockRegistry.TANK;
    }

    public BlockPos getMultiBlockMin() {
        return this.min.get();
    }

    public BlockPos getMultiBlockMax() {
        return this.max.get();
    }

    public boolean isMultiBlockOrigin() {
        BlockPos origin = this.getMultiBlockMin();
        return DEFAULT.equals(origin) || this.getBlockPos().equals(origin) || (this.origin != null && this.origin.getTile(this.getLevel()) == this);
    }

    @Override
    protected void tickComponent() {}

    @Override
    protected void runNetherLogic() {
        if (this.getBlockPos().getY() == (int) this.getWaterLevel()) {
            super.runNetherLogic();
        } else if(this.getBlockPos().getY() == this.getMultiBlockMax().getY() && this.getContent() == this.getCapacity()) {
            // For the first tick when the tank is completely full
            super.runNetherLogic();
        }
    }

    @Override
    protected float getNetherEvaporationRate() {
        return (super.getNetherEvaporationRate()*super.getCapacity())/this.getCapacity();
    }

    @Override
    public int getContent() {
        if(this.isMultiBlockOrigin()) {
            return super.getContent();
        } else {
            TileEntityIrrigationTank origin = this.getMultiBlockOrigin();
            if(origin != null) {
                // origin can be null if the origin chunk is not loaded
                return origin.getContent();
            }
            return 0;
        }
    }

    @Override
    public float getWaterLevel() {
        if(this.isMultiBlockOrigin()) {
            return super.getWaterLevel();
        } else {
            TileEntityIrrigationTank origin = this.getMultiBlockOrigin();
            if(origin != null) {
                // origin can be null if the origin chunk is not loaded
                return origin.getWaterLevel();
            }
            return 0;
        }
    }

    @Override
    public float getRenderLevel(float partialTicks) {
        if(this.isMultiBlockOrigin()) {
            float level = super.getRenderLevel(partialTicks);
            if(level == this.getMaxLevel()) {
                level = level - 0.0001F;
            }
            return level;
        } else {
            TileEntityIrrigationTank origin = this.getMultiBlockOrigin();
            if(origin != null) {
                // origin can be null if the origin chunk is not loaded
                return origin.getRenderLevel(partialTicks);
            }
            return 0;
        }
    }

    @Override
    public int pushWater(int max, boolean execute) {
        if(this.isMultiBlockOrigin()) {
            return super.pushWater(max, execute);
        } else {
            TileEntityIrrigationTank origin = this.getMultiBlockOrigin();
            if(origin != null) {
                // origin can be null if the origin chunk is not loaded
                return origin.pushWater(max, execute);
            }
            return 0;
        }
    }

    @Override
    public int drainWater(int max, boolean execute) {
        if(this.isMultiBlockOrigin()) {
            return super.drainWater(max, execute);
        } else {
            TileEntityIrrigationTank origin = this.getMultiBlockOrigin();
            if(origin != null) {
                // origin can be null if the origin chunk is not loaded
                return origin.drainWater(max, execute);
            }
            return 0;
        }
    }

    @Override
    protected void setContent(int content) {
        if(this.isMultiBlockOrigin()) {
            float before = this.getWaterLevel();
            super.setContent(content);
            this.updateMultiBlockFluidStates(before, this.getWaterLevel());
        } else {
            TileEntityIrrigationTank origin = this.getMultiBlockOrigin();
            if(origin != null) {
                // origin can be null if the origin chunk is not loaded
                origin.setContent(content);
            }
        }
    }

    @Override
    protected void setLevel(float level) {
        if(this.isMultiBlockOrigin()) {
            float before = this.getWaterLevel();
            super.setLevel(level);
            this.updateMultiBlockFluidStates(before, this.getWaterLevel());
        } else {
            TileEntityIrrigationTank origin = this.getMultiBlockOrigin();
            if(origin != null) {
                // origin can be null if the origin chunk is not loaded
                this.getMultiBlockOrigin().setLevel(level);
            }
        }
    }

    @Override
    public int getCapacity() {
        return super.getCapacity()
                *(this.getMultiBlockMax().getX() - this.getMultiBlockMin().getX() + 1)
                *(this.getMultiBlockMax().getY() - this.getMultiBlockMin().getY() + 1)
                *(this.getMultiBlockMax().getZ() - this.getMultiBlockMin().getZ() + 1);
    }

    @Override
    public float getMinLevel() {
        return MIN_Y + this.getMultiBlockMin().getY();
    }

    @Override
    public float getMaxLevel() {
        return MAX_Y + this.getMultiBlockMax().getY();
    }

    @Override
    protected int calculateContents(float height) {
        return (int) (this.getCapacity()*(height + this.getMultiBlockMin().getY() - this.getMinLevel())/(this.getMaxLevel() - this.getMinLevel()));
    }

    @Override
    protected float calculateHeight(int contents) {
        return this.getMinLevel() - this.getMultiBlockMin().getY() + (contents + 0.0F)*(this.getMaxLevel() - this.getMinLevel())/(this.getCapacity() + 0.0F);
    }

    @Nullable
    public TileEntityIrrigationTank getMultiBlockOrigin() {
        if(this.getLevel() == null || this.isMultiBlockOrigin()) {
            return this;
        }
        if(this.origin != null) {
            return this.origin.getTile(this.getLevel());
        }

        this.origin = new TileReference<>(this.getMultiBlockMin(), TANK_GETTER);
        TileEntityIrrigationTank tank = this.origin.getTile(this.getLevel());

        if(this.origin.isRemoved()) {
            this.unFormMultiBlock();
            return this;
        }

        if(this.origin.isUnloaded()) {
            return null;
        }
        return tank;
    }

    public void checkAndFormMultiBlock() {
        // Safety check
        if(this.getLevel() == null || this.getLevel().isClientSide()) {
            return;
        }
        // Set min and max
        this.min.set(this.getBlockPos());
        this.max.set(this.getBlockPos());
        // Form multi-block
        new MultiBlockFormer(this).formMultiBlock();
    }

    public void onNeighbourChanged(BlockPos fromPos) {
        if(this.getLevel() == null) {
            return;
        }
        if(isInMultiBlock(fromPos)) {
            BlockEntity tile = this.getLevel().getBlockEntity(fromPos);
            if(tile instanceof TileEntityIrrigationTank) {
                TileEntityIrrigationTank tank = (TileEntityIrrigationTank) tile;
                if(tank.isSameMaterial(this)) {
                    if(tank.getMultiBlockMin().equals(this.getMultiBlockMin()) && tank.getMultiBlockMax().equals(this.getMultiBlockMax())) {
                        return;
                    }
                }
            }
            this.unFormMultiBlock();
        }
    }

    public boolean isInMultiBlock(BlockPos pos) {
        BlockPos min = this.getMultiBlockMin();
        BlockPos max = this.getMultiBlockMax();
        return pos.getX() >= min.getX()
                && pos.getY() >= min.getY()
                && pos.getZ() >= min.getZ()
                && pos.getX() <= max.getX()
                && pos.getY() <= max.getY()
                && pos.getZ() <= max.getZ();
    }

    public void unFormMultiBlock() {
        // Safety check
        if(this.getLevel() == null) {
            return;
        }
        float level = super.getWaterLevel();
        BlockPos min = new BlockPos(this.getMultiBlockMin());
        BlockPos max = new BlockPos(this.getMultiBlockMax());
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(0, 0, 0);
        for(int x = min.getX(); x <= max.getX(); x++) {
            for(int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    pos.set(x, y, z);
                    BlockEntity tile = this.getLevel().getBlockEntity(pos);
                    if(tile instanceof TileEntityIrrigationTank) {
                        TileEntityIrrigationTank tank = (TileEntityIrrigationTank) tile;
                        tank.origin = this.getLevel().isClientSide() ? new TileReference<>(tank.getBlockPos(), TANK_GETTER) : null;
                        tank.min.set(tank.getBlockPos());
                        tank.max.set(tank.getBlockPos());
                        this.getLevel().setBlock(tank.getBlockPos(), AgriBlockRegistry.TANK.defaultBlockState(), 3);
                        tank.setLevel(level);
                    }
                }
            }
        }
    }

    protected void updateMultiBlockFluidStates(float before, float after) {
        Level world = this.getLevel();
        if(world == null || world.isClientSide()) {
            return;
        }
        // map before and after to ordered variables
        float d1 = Math.min(before, after);
        float d2 = Math.max(before, after);
        // check if the change was significant
        float delta = d2 - d1;
        if(delta < 1) {
            d1 = d1 - (int) d1;
            d2 = d2 - (int) d2;
            if(d1 >= 0.5 || d2 < 0.5) {
                return;
            }
        }
        // apply the change to the relevant blocks
        BlockPos min = this.getMultiBlockMin();
        BlockPos max = this.getMultiBlockMax();
        int y1 = Math.max((int) d1, min.getY());
        int y2 = Math.min(1 + (int) d2, max.getY());
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(0, 0, 0);
        for(int x = min.getX(); x <= max.getX(); x++) {
            for(int y = y1; y <= y2; y++) {
                for(int z = min.getZ(); z <= max.getZ(); z++) {
                    pos.set(x, y, z);
                    this.getBlock().updateFluidState(this.getLevel(), pos, world.getBlockState(pos), after);
                }
            }
        }
    }

    @Override
    protected boolean canConnect(TileEntityIrrigationComponent other) {
        return other instanceof TileEntityIrrigationChannel && other.isSameMaterial(this);
    }

    @Override
    protected boolean canTransfer(TileEntityIrrigationComponent other, Direction dir) {
        return this.canConnect(other);
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
        return "Tank";
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return tank == 0 ? new FluidStack(Fluids.WATER, this.getContent()) : FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 ? this.getCapacity() : 0;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return tank == 0 && stack.getFluid() == Fluids.WATER;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return resource.getFluid() == Fluids.WATER ? this.pushWater(resource.getAmount(), action.execute()) : 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return resource.getFluid() == Fluids.WATER ? this.drain(resource.getAmount(), action) : FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        int drained = this.drainWater(maxDrain, action.execute());
        return drained > 0 ? new FluidStack(Fluids.WATER, drained) : FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return this.capability.cast();
        }
        return super.getCapability(cap, side);
    }

    public static class MultiBlockFormer {
        private final Level world;
        private final ItemStack material;

        private final BlockPos.MutableBlockPos min;
        private final BlockPos.MutableBlockPos max;

        private final Set<Unchecked> unchecked;
        private final Map<BlockPos, Checked> checked;

        protected MultiBlockFormer(TileEntityIrrigationTank tank) {
            this.world = Objects.requireNonNull(tank.getLevel(), "Invalid state, can not form multi-block with non-world tile entity");
            this.material = tank.getMaterial().copy();
            this.min = tank.getBlockPos().mutable();
            this.max = tank.getBlockPos().mutable();
            this.unchecked = Sets.newConcurrentHashSet();
            this.checked = Maps.newHashMap();
            this.unchecked.add(new Unchecked(tank));
        }

        public final Level getWorld() {
            return this.world;
        }

        public final boolean checkMaterial(ItemStack material) {
            return ItemStack.matches(this.material, material);
        }

        public void formMultiBlock() {
            // scan the region to check if a multi-block can be formed
            if(!this.scanRegion()) {
                return;
            }
            // counter for the fluid contents
            int counter = 0;
            // form the multi-block
            for (int x = this.min.getX(); x <= this.max.getX(); x++) {
                for (int y = this.min.getY(); y <= this.max.getY(); y++) {
                    for (int z = this.min.getZ(); z <= this.max.getZ(); z++) {
                        // Fetch tank
                        TileEntityIrrigationTank tank = this.checked.get(new BlockPos(x, y, z)).getTank();
                        // Make sure it is not in a multi-block state
                        tank.unFormMultiBlock();
                        // Add its water contents to the counter and empty it
                        counter += tank.getContent();
                        tank.setContent(0);
                        // Set the multi-block limits
                        tank.min.set(new BlockPos(this.min));
                        tank.max.set(new BlockPos(this.max));
                        // Fetch the block state
                        BlockState state = tank.getBlockState();
                        state = this.handleDirection(state, tank, Direction.WEST, x == this.min.getX());
                        state = this.handleDirection(state, tank, Direction.EAST, x == this.max.getX());
                        state = this.handleDirection(state, tank, Direction.DOWN, y == this.min.getY());
                        state = this.handleDirection(state, tank, Direction.NORTH, z == this.min.getZ());
                        state = this.handleDirection(state, tank, Direction.SOUTH, z == this.max.getZ());
                        // Update the block state
                        this.getWorld().setBlock(tank.getBlockPos(), state, 3);
                    }
                }
            }
            // Set the fluid contents
            final int contents = counter;
            this.checked.values().stream().findAny().map(Checked::getTank).ifPresent(tank -> tank.setContent(contents));
        }

        protected boolean scanRegion() {
            Iterator<Unchecked> iterator = this.unchecked.iterator();
            while(iterator.hasNext()) {
                // Retrieve next from the iterator and remove
                Unchecked next = iterator.next();
                iterator.remove();
                BlockPos pos = next.getPos();
                // Check if the position is valid
                boolean stop = next.check(this).map(checked -> {
                    // Add the checked spot to the checked map
                    this.checked.put(pos, checked);
                    // Expand the unchecked region to cover the tank's multi-block
                    this.expand(checked.getTank());
                    // Check neighbouring blocks if there are other tanks
                    Arrays.stream(Direction.values()).forEach(dir ->  this.scanPos(pos.relative(dir)));
                    return false;
                }).orElse(true);
                // If an invalid position was found, return false
                if(stop) {
                    return false;
                }
            }
            // More unchecked positions might have been added, check these recursively
            if(this.unchecked.size() > 0) {
                return this.scanRegion();
            }
            // All positions have been checked without error, return true
            return true;
        }

        protected void scanPos(BlockPos pos) {
            if(this.checked.containsKey(pos)) {
                // Already visited
                return;
            }
            Unchecked unchecked = new Unchecked(this, pos);
            if(this.unchecked.contains(unchecked)) {
                // Already on the check list
                return;
            }
            // Add the new spot to the check list if it is a valid neighbour
            if(unchecked.isValid(this)) {
                this.unchecked.add(unchecked);
            }
        }

        protected void expand(TileEntityIrrigationTank tank) {
            this.expand(tank.getMultiBlockMin());
            this.expand(tank.getMultiBlockMax());
        }

        protected void expand(BlockPos pos) {
            // Check max x
            if (this.max.getX() < pos.getX()) {
                // add new sites
                this.addSites(this.max.getX() + 1, this.min.getY(), this.min.getZ(), pos.getX(), this.max.getY(), this.max.getZ());
                // Update max
                this.max.set(pos.getX(), this.max.getY(), this.max.getZ());
            }
            // Check max y
            if (this.max.getY() < pos.getY()) {
                // add new sites
                this.addSites(this.min.getX(), this.max.getY() + 1, this.min.getZ(), this.max.getX(), pos.getY(), this.max.getZ());
                // Update max
                this.max.set(this.max.getX(), pos.getY(), this.max.getZ());
            }
            // Check max z
            if (this.max.getZ() < pos.getZ()) {
                // add new sites
                this.addSites(this.min.getX(), this.min.getY(), this.max.getZ() + 1, this.max.getX(), this.max.getY(), pos.getZ());
                // Update max
                this.max.set(this.max.getX(), this.max.getY(), pos.getZ());
            }
            // Check min x
            if (this.min.getX() > pos.getX()) {
                // add new sites
                this.addSites(pos.getX(), this.min.getY(), this.min.getZ(), this.min.getX() - 1, this.max.getY(), this.max.getZ());
                // Update min
                this.min.set(pos.getX(), this.min.getY(), this.min.getZ());
            }
            // Check min y
            if (this.min.getY() > pos.getY()) {
                // add new sites
                this.addSites(this.min.getX(), pos.getY(), this.min.getZ(), this.max.getX(), this.min.getY() - 1, this.max.getZ());
                // Update min
                this.min.set(this.min.getX(), pos.getY(), this.min.getZ());
            }
            // Check min z
            if (this.min.getZ() > pos.getZ()) {
                // add new sites
                this.addSites(this.min.getX(), this.min.getY(), pos.getZ(), this.max.getX(), this.max.getY(), this.min.getZ() - 1);
                // Update min
                this.min.set(this.min.getX(), this.min.getY(), pos.getZ());
            }
        }

        protected void addSites(int x1, int y1, int z1, int x2, int y2, int z2) {
            for (int x = x1; x <= x2; x++) {
                for (int y = y1; y <= y2; y++) {
                    for (int z = z1; z <= z2; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        if(!this.checked.containsKey(pos)) {
                            this.unchecked.add(new Unchecked(this, pos));
                        }
                    }
                }
            }
        }

        protected BlockState handleDirection(final BlockState state, TileEntityIrrigationTank tank, Direction dir, boolean edge) {
            if (tank.getLevel() == null || tank.getLevel().isClientSide()) {
                return state;
            }
            if (dir.getAxis().isVertical()) {
                return BlockIrrigationTank.DOWN.apply(state, !edge);
            }
            return BlockIrrigationTank.getConnection(dir)
                    .map((connection) -> {
                        if (edge) {
                            BlockPos offset = tank.getBlockPos().relative(dir);
                            BlockEntity tile = tank.getLevel().getBlockEntity(offset);
                            if (tile instanceof TileEntityIrrigationChannel) {
                                if (((TileEntityIrrigationChannel) tile).canConnect(tank)) {
                                    // The block is next to a channel, update channel block state
                                    BlockIrrigationChannelAbstract.getConnection(dir.getOpposite()).ifPresent(channelCon ->
                                            tank.getLevel().setBlock(offset, channelCon.apply(tile.getBlockState(), true), 3));
                                    // And return the tank block state
                                    return connection.apply(state, BlockIrrigationTank.Connection.CHANNEL);
                                }
                            }
                        } else {
                            // The block is not near an edge
                            return connection.apply(state, BlockIrrigationTank.Connection.TANK);
                        }
                        return connection.apply(state, BlockIrrigationTank.Connection.NONE);
                    }).orElse(state);
        }

        public static class Unchecked {
            private final BlockPos pos;
            private final BlockEntity tile;

            public Unchecked(BlockEntity tile) {
                this.pos = tile.getBlockPos();
                this.tile = tile;
            }

            public Unchecked(MultiBlockFormer former, BlockPos pos) {
                this.pos = pos;
                this.tile = former.getWorld().getBlockEntity(pos);
            }

            public final BlockPos getPos() {
                return this.pos;
            }

            public boolean isValid(MultiBlockFormer former) {
                if(this.tile instanceof TileEntityIrrigationTank) {
                    TileEntityIrrigationTank tank = (TileEntityIrrigationTank) tile;
                    return former.checkMaterial(tank.getMaterial());
                }
                return false;
            }

            public Optional<Checked> check(MultiBlockFormer former) {
                return Optional.ofNullable(this.isValid(former) ? new Checked((TileEntityIrrigationTank) this.tile) : null);
            }

            @Override
            public int hashCode() {
                return this.getPos().hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                if(obj == this) {
                    return true;
                }
                if(obj instanceof Unchecked) {
                    return this.getPos().equals(((Unchecked) obj).getPos());
                }
                return false;
            }
        }

        public static class Checked {
            private final TileEntityIrrigationTank tank;

            private Checked(TileEntityIrrigationTank tank) {
                this.tank = tank;
            }

            public TileEntityIrrigationTank getTank() {
                return this.tank;
            }
        }
    }

    public static RenderFactory createRenderFactory() {
        return new RenderFactory();
    }

    private static class RenderFactory implements InfinityTileEntityType.IRenderFactory<TileEntityIrrigationTank> {
        @Nullable
        @OnlyIn(Dist.CLIENT)
        public TileEntityIrrigationTankRenderer createRenderer() {
            return new TileEntityIrrigationTankRenderer();
        }
    }
}
