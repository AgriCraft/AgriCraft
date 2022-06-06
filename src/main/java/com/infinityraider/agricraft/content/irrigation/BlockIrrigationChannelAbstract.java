package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.infinitylib.block.BlockDynamicTexture;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BlockIrrigationChannelAbstract extends BlockDynamicTexture<TileEntityIrrigationChannel> {
    // Properties
    public static final InfProperty<Boolean> NORTH = InfProperty.Creators.create("north", false);
    public static final InfProperty<Boolean> EAST = InfProperty.Creators.create("east", false);
    public static final InfProperty<Boolean> SOUTH = InfProperty.Creators.create("south", false);
    public static final InfProperty<Boolean> WEST = InfProperty.Creators.create("west", false);

    public static final InfProperty<Valve> VALVE = InfProperty.Creators.create("valve", Valve.class, Valve.NONE);

    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder()
            .add(NORTH).add(EAST).add(SOUTH).add(WEST)
            .add(VALVE)
            .build();

    public static Optional<InfProperty<Boolean>> getConnection(Direction direction) {
        switch (direction) {
            case NORTH:
                return Optional.of(NORTH);
            case SOUTH:
                return Optional.of(SOUTH);
            case EAST:
                return Optional.of(EAST);
            case WEST:
                return Optional.of(WEST);
        }
        return Optional.empty();
    }

    // TileEntity factory
    private static final BiFunction<BlockPos, BlockState, TileEntityIrrigationChannel> TILE_FACTORY = TileEntityIrrigationChannel::new;

    private final boolean handWheel;

    public BlockIrrigationChannelAbstract(String name, boolean handWheel, Properties properties) {
        super(name, properties.strength(2, 3));
        this.handWheel = handWheel;
    }

    public boolean hasHandWheel() {
        return this.handWheel;
    }

    @Override
    public void addDrops(Consumer<ItemStack> dropAcceptor, BlockState state, TileEntityIrrigationChannel tile, LootContext.Builder context) {
        if(VALVE.fetch(state).hasValve()) {
            dropAcceptor.accept(new ItemStack(AgriApi.getAgriContent().getItems().getValveItem(), 1));
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = this.defaultBlockState();
        for(Direction dir : Direction.values()) {
            Optional<InfProperty<Boolean>> prop = getConnection(dir);
            if(prop.isPresent()) {
                BlockEntity tile = world.getBlockEntity(pos.relative(dir));
                if(tile instanceof TileEntityIrrigationComponent) {
                    TileEntityIrrigationComponent component = (TileEntityIrrigationComponent) tile;
                    if(component.isSameMaterial(context.getItemInHand())) {
                        state = prop.get().apply(state, true);
                    }
                }
            }
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState ownState, Direction dir, BlockState otherState, LevelAccessor world, BlockPos pos, BlockPos otherPos) {
        return getConnection(dir).map(prop -> {
            BlockEntity otherTile = world.getBlockEntity(otherPos);
            BlockEntity ownTile = world.getBlockEntity(pos);
            if(ownTile instanceof TileEntityIrrigationChannel && otherTile instanceof TileEntityIrrigationComponent) {
                TileEntityIrrigationComponent other = (TileEntityIrrigationComponent) otherTile;
                if (other.canConnect((TileEntityIrrigationChannel) ownTile)) {
                    return prop.apply(ownState, true);
                }
            }
            return prop.apply(ownState, false);
        }).orElse(super.updateShape(ownState, dir, otherState, world, pos, otherPos));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileEntityIrrigationChannel) {
            ((TileEntityIrrigationChannel) tile).onNeighbourUpdate(fromPos);
        }
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack, @Nullable BlockEntity tile) {
        if(tile instanceof TileEntityIrrigationChannel) {
            TileEntityIrrigationChannel channel = (TileEntityIrrigationChannel) tile;
            channel.onNeighbourUpdate(Direction.NORTH);
            channel.onNeighbourUpdate(Direction.EAST);
            channel.onNeighbourUpdate(Direction.SOUTH);
            channel.onNeighbourUpdate(Direction.WEST);
        }
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public BiFunction<BlockPos, BlockState, TileEntityIrrigationChannel> getTileEntityFactory() {
        return TILE_FACTORY;
    }

    public void playValveSound(Level world, BlockPos pos) {
        world.playSound(null, pos, AgriApi.getAgriContent().getSounds().getValveSound(), SoundSource.BLOCKS,
                2.5F, 2.6F + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.8F);
    }

    public enum Valve implements StringRepresentable {
        NONE(true, false),
        OPEN(true, true),
        CLOSED(false, true);

        private final boolean transfer;
        private final boolean valve;

        Valve(boolean transfer, boolean valve) {
            this.transfer = transfer;
            this.valve = valve;
        }

        public boolean canTransfer() {
            return this.transfer;
        }

        public boolean hasValve() {
            return this.valve;
        }

        public Valve toggleValve() {
            if(this.hasValve()) {
                return this.canTransfer() ? CLOSED : OPEN;
            }
            return NONE;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}

