package com.infinityraider.agricraft.content.irrigation;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import com.infinityraider.agricraft.capability.CapabilityIrrigationComponent;
import com.infinityraider.agricraft.capability.CapabilityMultiBlockData;
import com.infinityraider.infinitylib.reference.Constants;
import com.infinityraider.infinitylib.utility.WorldHelper;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntityIrrigationTank extends TileEntityIrrigationComponent implements IAgriIrrigationNode {
    private static final double MIN_Y = Constants.UNIT;
    private static final double MAX_Y = 1;

    private final AutoSyncedField<BlockPos> min;
    private final AutoSyncedField<BlockPos> max;

    public TileEntityIrrigationTank() {
        super(AgriCraft.instance.getModTileRegistry().irrigation_tank, AgriCraft.instance.getConfig().tankCapacity(), MIN_Y, MAX_Y);
        this.min = this.getAutoSyncedFieldBuilder(new BlockPos(0, 0, 0)).build();
        this.max = this.getAutoSyncedFieldBuilder(new BlockPos(0, 0, 0)).build();
    }

    public BlockPos getMultiBlockMin() {
        return this.min.get();
    }

    public BlockPos getMultiBlockMax() {
        return this.max.get();
    }

    public TileEntityIrrigationTank getMultiBlockOrigin() {
        if(this.getWorld() == null) {
            return this;
        }
        if(this.getPos().equals(this.getMultiBlockMin())) {
            return this;
        }
        TileEntity tile = this.getWorld().getTileEntity(this.getMultiBlockMin());
        if(tile instanceof TileEntityIrrigationTank) {
            return (TileEntityIrrigationTank) tile;
        }
        this.unFormMultiBlock();
        return this;
    }

    public void checkAndFormMultiBlock() {
        // Safety check
        if(this.getWorld() == null || this.getWorld().isRemote()) {
            return;
        }
        // Set min and max
        this.min.set(this.getPos());
        this.max.set(this.getPos());
        // Form multi-block
        new MultiBlockFormer(this).formMultiBlock();
    }

    public void onNeighbourChanged(BlockPos fromPos) {
        if(this.getWorld() == null) {
            return;
        }
        if(isInMultiBlock(fromPos)) {
            TileEntity tile = this.getWorld().getTileEntity(fromPos);
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
        if(this.getWorld() == null) {
            return;
        }
        BlockPos min = new BlockPos(this.getMultiBlockMin());
        BlockPos max = new BlockPos(this.getMultiBlockMax());
        BlockPos.Mutable pos = new BlockPos.Mutable(0, 0, 0);
        for(int x = min.getX(); x <= max.getX(); x++) {
            for(int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    pos.setPos(x, y, z);
                    TileEntity tile = this.getWorld().getTileEntity(pos);
                    if(tile instanceof TileEntityIrrigationTank) {
                        TileEntityIrrigationTank tank = (TileEntityIrrigationTank) tile;
                        tank.min.set(tank.getPos());
                        tank.max.set(tank.getPos());
                        this.getWorld().setBlockState(tank.getPos(), AgriCraft.instance.getModBlockRegistry().tank.getDefaultState());
                    }
                }
            }
        }
        CapabilityMultiBlockData.getInstance().removeMultiBlockNode(this.getWorld(), min);
    }

    @Override
    public Optional<IAgriIrrigationNode> getNode(Direction side) {
        return Optional.of(CapabilityMultiBlockData.getInstance().getIrrigationNode(this));
    }

    public IAgriIrrigationNode createNewMultiBlockNode() {
        if(this.getWorld() == null) {
            // Shouldn't ever happen, but you know, better safe than sorry...
            return this;
        }
        if(this.getMultiBlockMin().equals(this.getMultiBlockMax())) {
            return this;
        }
        return new MultiBlockNode(
                this.getWorld(),
                this.getMultiBlockMin(),
                this.getMultiBlockMax(),
                this.getMaterial().copy(),
                () -> this.getMultiBlockOrigin().getFluidContents(),
                (v) -> this.getMultiBlockOrigin().setFluidContents(v)
        );
    }

    @Override
    public boolean canConnect(@Nonnull IAgriIrrigationNode other, Direction from) {
        if(from.getAxis().isVertical()) {
            return false;
        }
        if(other instanceof TileEntityIrrigationChannel) {
            return this.isSameMaterial((TileEntityIrrigationChannel) other);
        }
        return false;
    }

    @Override
    public boolean isSource() {
        return true;
    }

    @Override
    public boolean isSink() {
        return false;
    }

    @Override
    protected void writeTileNBT(@Nonnull CompoundNBT tag) {

    }

    @Override
    protected void readTileNBT(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {

    }

    public static class MultiBlockFormer {
        private final World world;
        private final ItemStack material;

        private final BlockPos.Mutable min;
        private final BlockPos.Mutable max;

        private final Set<Unchecked> unchecked;
        private final Map<BlockPos, Checked> checked;

        protected MultiBlockFormer(TileEntityIrrigationTank tank) {
            this.world = Objects.requireNonNull(tank.getWorld(), "Invalid state, can not form multi-block with non-world tile entity");
            this.material = tank.getMaterial().copy();
            this.min = tank.getPos().toMutable();
            this.max = tank.getPos().toMutable();
            this.unchecked = Sets.newConcurrentHashSet();
            this.checked = Maps.newHashMap();
            this.unchecked.add(new Unchecked(tank));
        }

        public final World getWorld() {
            return this.world;
        }

        public final boolean checkMaterial(ItemStack material) {
            return ItemStack.areItemsEqual(this.material, material);
        }

        public void formMultiBlock() {
            if(!this.scanRegion()) {
                return;
            }
            for (int x = this.min.getX(); x <= this.max.getX(); x++) {
                for (int y = this.min.getY(); y <= this.max.getY(); y++) {
                    for (int z = this.min.getZ(); z <= this.max.getZ(); z++) {
                        // Fetch tank
                        TileEntityIrrigationTank tank = this.checked.get(new BlockPos(x, y, z)).getTank();
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
                        this.getWorld().setBlockState(tank.getPos(), state);
                    }
                }
            }
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
                    Arrays.stream(Direction.values()).forEach(dir ->  this.scanPos(pos.offset(dir)));
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
                this.max.setPos(pos.getX(), this.max.getY(), this.max.getZ());
            }
            // Check max y
            if (this.max.getY() < pos.getY()) {
                // add new sites
                this.addSites(this.min.getX(), this.max.getY() + 1, this.min.getZ(), this.max.getX(), pos.getY(), this.max.getZ());
                // Update max
                this.max.setPos(this.max.getX(), pos.getY(), this.max.getZ());
            }
            // Check max z
            if (this.max.getZ() < pos.getZ()) {
                // add new sites
                this.addSites(this.min.getX(), this.min.getY(), this.max.getZ() + 1, this.max.getX(), this.max.getY(), pos.getZ());
                // Update max
                this.max.setPos(this.max.getX(), this.max.getY(), pos.getZ());
            }
            // Check min x
            if (this.min.getX() > pos.getX()) {
                // add new sites
                this.addSites(pos.getX(), this.min.getY(), this.min.getZ(), this.min.getX() - 1, this.max.getY(), this.max.getZ());
                // Update min
                this.min.setPos(pos.getX(), this.min.getY(), this.min.getZ());
            }
            // Check min y
            if (this.min.getY() > pos.getY()) {
                // add new sites
                this.addSites(this.min.getX(), pos.getY(), this.min.getZ(), this.max.getX(), this.min.getY() - 1, this.max.getZ());
                // Update min
                this.min.setPos(this.min.getX(), pos.getY(), this.min.getZ());
            }
            // Check min z
            if (this.min.getZ() > pos.getZ()) {
                // add new sites
                this.addSites(this.min.getX(), this.min.getY(), pos.getZ(), this.max.getX(), this.max.getY(), this.min.getZ() - 1);
                // Update min
                this.min.setPos(this.min.getX(), this.min.getY(), pos.getZ());
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
            if (tank.getWorld() == null || tank.getWorld().isRemote()) {
                return state;
            }
            if (dir.getAxis().isVertical()) {
                return BlockIrrigationTank.DOWN.apply(state, !edge);
            }
            return BlockIrrigationTank.getConnection(dir)
                    .map((connection) -> {
                        if (edge) {
                            BlockPos offset = tank.getPos().offset(dir);
                            TileEntity tile = tank.getWorld().getTileEntity(offset);
                            if (tile instanceof TileEntityIrrigationChannel) {
                                if (ItemStack.areItemsEqual(tank.getMaterial(), ((TileEntityIrrigationChannel) tile).getMaterial())) {
                                    // The block is next to a channel, update channel block state
                                    BlockIrrigationChannelAbstract.getConnection(dir.getOpposite()).ifPresent(channelCon ->
                                            tank.getWorld().setBlockState(offset, channelCon.apply(tile.getBlockState(), true)));
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
            private final TileEntity tile;

            public Unchecked(TileEntity tile) {
                this.pos = tile.getPos();
                this.tile = tile;
            }

            public Unchecked(MultiBlockFormer former, BlockPos pos) {
                this.pos = pos;
                this.tile = former.getWorld().getTileEntity(pos);
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

    public static class MultiBlockNode implements IAgriIrrigationNode {
        private final World world;
        private final BlockPos min;
        private final BlockPos max;
        private final ItemStack material;
        private final IntSupplier contentGetter;
        private final IntConsumer contentSetter;

        private Set<Tuple<Direction, BlockPos>> connections;

        protected MultiBlockNode(World world, BlockPos min, BlockPos max, ItemStack material, IntSupplier contentGetter, IntConsumer contentSetter) {
            this.world = world;
            this.min = min;
            this.max = max;
            this.material = material;
            this.contentGetter = contentGetter;
            this.contentSetter = contentSetter;
        }

        public World getWorld() {
            return this.world;
        }

        public BlockPos getMin() {
            return this.min;
        }

        public BlockPos getMax() {
            return this.max;
        }

        public ItemStack getMaterial() {
            return this.material;
        }

        public int getTankCount() {
            return this.getCount(BlockPos::getX) * this.getCount(BlockPos::getY) * this.getCount(BlockPos::getZ);
        }

        protected int getCount(ToIntFunction<BlockPos> counter) {
            return counter.applyAsInt(this.getMax()) - counter.applyAsInt(this.getMin()) + 1;
        }

        @Override
        public Collection<IAgriIrrigationComponent> getComponents() {
            return WorldHelper.collectCapabilities(this.getWorld(), this.getMin(), this.getMax(),
                    CapabilityIrrigationComponent.getInstance().getCapability(), IAgriIrrigationComponent.class);
        }

        @Override
        public double getMinFluidHeight() {
            return this.getMin().getY() + MIN_Y;
        }

        @Override
        public double getMaxFluidHeight() {
            return this.getMax().getY() + MAX_Y;
        }

        @Override
        public int getFluidCapacity() {
            return this.getTankCount() * AgriCraft.instance.getConfig().tankCapacity();
        }

        @Override
        public int getFluidContents() {
            return this.contentGetter.getAsInt();
        }

        @Override
        public void setFluidContents(int volume) {
            this.contentSetter.accept(volume);
        }

        @Override
        public boolean canConnect(IAgriIrrigationNode other, Direction from) {
            if(from.getAxis().isVertical()) {
                return false;
            }
            if(other instanceof TileEntityIrrigationComponent) {
                return ((TileEntityIrrigationComponent) other).isSameMaterial(this.getMaterial());
            }
            return false;
        }

        @Override
        public Set<Tuple<Direction, BlockPos>> getPotentialConnections() {
            if(this.connections == null) {
                ImmutableSet.Builder<Tuple<Direction, BlockPos>> setBuilder = new ImmutableSet.Builder<>();
                for(int y = this.getMin().getY(); y <= this.getMax().getY(); y++) {
                    for(int x = this.getMin().getX(); x <= this.getMax().getX(); x++) {
                        setBuilder.add(new Tuple<>(Direction.NORTH, new BlockPos(x, y, this.getMin().getZ() - 1)));
                        setBuilder.add(new Tuple<>(Direction.SOUTH, new BlockPos(x, y, this.getMax().getZ() + 1)));
                    }
                    for(int z = this.getMin().getZ(); z <= this.getMax().getZ(); z++) {
                        setBuilder.add(new Tuple<>(Direction.WEST, new BlockPos(this.getMin().getX() - 1, y, z)));
                        setBuilder.add(new Tuple<>(Direction.EAST, new BlockPos(this.getMax().getX() + 1, y, z)));
                    }
                }
                this.connections = setBuilder.build();
            }
            return this.connections;
        }

        @Override
        public boolean isSource() {
            return true;
        }

        @Override
        public boolean isSink() {
            return false;
        }
    }
}
