package com.infinityraider.agricraft.content.irrigation;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.AgriFluidRegistry;
import com.infinityraider.agricraft.content.AgriItemRegistry;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.block.BlockDynamicTexture;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import com.infinityraider.infinitylib.reference.Constants;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidAttributes;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockIrrigationTank extends BlockDynamicTexture<TileEntityIrrigationTank> {
    // Properties
    public static final InfProperty<Connection> NORTH = InfProperty.Creators.create("north", Connection.class, Connection.NONE);
    public static final InfProperty<Connection> EAST = InfProperty.Creators.create("east", Connection.class, Connection.NONE);
    public static final InfProperty<Connection> SOUTH = InfProperty.Creators.create("south", Connection.class, Connection.NONE);
    public static final InfProperty<Connection> WEST = InfProperty.Creators.create("west", Connection.class, Connection.NONE);
    public static final InfProperty<Boolean> DOWN = InfProperty.Creators.create("down", false);
    public static final InfProperty<Boolean> LADDER = InfProperty.Creators.create("ladder", false);
    public static final InfProperty<Boolean> WATER = InfProperty.Creators.create("water", false);

    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder()
            .add(NORTH).add(EAST).add(SOUTH).add(WEST).add(DOWN)
            .add(LADDER).add(WATER)
            .build();

    public static Optional<InfProperty<Connection>> getConnection(Direction direction) {
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
    private static final BiFunction<BlockPos, BlockState, TileEntityIrrigationTank> TILE_FACTORY = TileEntityIrrigationTank::new;

    // VoxelShapes
    public static final VoxelShape SHAPE_DOWN = Block.box(0, 0, 0, 16, 2, 16);

    private static final Map<BlockState, VoxelShape> SHAPES = Maps.newConcurrentMap();

    public static VoxelShape getShape(BlockState state) {
        if (!(state.getBlock() instanceof BlockIrrigationTank)) {
            return Shapes.empty();
        }
        return SHAPES.computeIfAbsent(state, (aState) ->
                Stream.concat(
                        Arrays.stream(Direction.values())
                                .filter(direction -> direction.getAxis().isHorizontal())
                                .map(direction -> getConnection(direction)
                                        .map(connection -> connection.fetch(aState))
                                        .map(connection -> connection.getShape(direction)))
                                .filter(Optional::isPresent)
                                .map(Optional::get),
                        Stream.of(DOWN.fetch(aState) ? Shapes.empty() : SHAPE_DOWN)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElse(Shapes.block()));
    }

    public BlockIrrigationTank() {
        super(Names.Blocks.TANK, Properties.of(Material.WOOD));
    }

    @Override
    public ItemIrrigationTank asItem() {
        return AgriItemRegistry.getInstance().tank.get();
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public BiFunction<BlockPos, BlockState, TileEntityIrrigationTank> getTileEntityFactory() {
        return TILE_FACTORY;
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
            ItemStack stack = player.getItemInHand(hand);
            if(stack.getItem() instanceof BucketItem) {
                BucketItem bucket = (BucketItem) stack.getItem();
                Fluid fluid = bucket.getFluid();
                if(fluid == Fluids.WATER) {
                    // try to fill from a bucket
                    BlockEntity tile = world.getBlockEntity(pos);
                    if(tile instanceof TileEntityIrrigationTank) {
                        TileEntityIrrigationTank tank = (TileEntityIrrigationTank) tile;
                        if(tank.pushWater(FluidAttributes.BUCKET_VOLUME, false) == FluidAttributes.BUCKET_VOLUME) {
                            tank.pushWater(FluidAttributes.BUCKET_VOLUME, true);
                            if(!player.isCreative()) {
                                player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                            }
                            return InteractionResult.SUCCESS;
                        }
                    }
                } else if(fluid == Fluids.EMPTY) {
                    // try to drain to a bucket
                    BlockEntity tile = world.getBlockEntity(pos);
                    if(tile instanceof TileEntityIrrigationTank) {
                        TileEntityIrrigationTank tank = (TileEntityIrrigationTank) tile;
                        if(tank.drainWater(FluidAttributes.BUCKET_VOLUME, false) == FluidAttributes.BUCKET_VOLUME) {
                            tank.drainWater(FluidAttributes.BUCKET_VOLUME, true);
                            player.setItemInHand(hand, new ItemStack(Items.WATER_BUCKET));
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            } else if(stack.getItem() == Items.LADDER) {
                // try to place a ladder
                if(!LADDER.fetch(state)) {
                    if(!world.isClientSide()) {
                        world.setBlock(pos, LADDER.apply(state, true), 3);
                        if(!player.isCreative()) {
                            player.getItemInHand(hand).shrink(1);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        return InteractionResult.FAIL;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
                            ItemStack stack, @Nullable BlockEntity tile) {
        if(tile instanceof TileEntityIrrigationTank) {
            ((TileEntityIrrigationTank) tile).checkAndFormMultiBlock();
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState ownState, Direction dir, BlockState otherState, LevelAccessor world, BlockPos pos, BlockPos otherPos) {
        return getConnection(dir).map(prop -> {
            if(prop.fetch(ownState).isTank()) {
                // tank logic is handled from within the tile entity
                return ownState;
            }
            BlockEntity ownTile = world.getBlockEntity(pos);
            BlockEntity otherTile = world.getBlockEntity(otherPos);
            if(ownTile instanceof TileEntityIrrigationTank && otherTile instanceof TileEntityIrrigationChannel) {
                TileEntityIrrigationChannel channel = (TileEntityIrrigationChannel) otherTile;
                if (channel.canConnect((TileEntityIrrigationTank) ownTile)) {
                    return prop.apply(ownState, Connection.CHANNEL);
                }
            }
            return prop.apply(ownState, Connection.NONE);
        }).orElse(super.updateShape(ownState, dir, otherState, world, pos, otherPos));
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileEntityIrrigationTank) {
            TileEntityIrrigationTank tank = (TileEntityIrrigationTank) tile;
            tank.onNeighbourChanged(fromPos);
            tank.onNeighbourUpdate(fromPos);
        }
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
    }

    @Override
    public void handlePrecipitation(BlockState state, Level world, BlockPos pos, Biome.Precipitation precipitation) {
        if(precipitation == Biome.Precipitation.RAIN) {
            int rate = AgriCraft.instance.getConfig().rainFillRate();
            if (rate > 0) {
                BlockEntity tile = world.getBlockEntity(pos);
                if (tile instanceof TileEntityIrrigationTank) {
                    TileEntityIrrigationTank tank = (TileEntityIrrigationTank) tile;
                    tank.pushWater(rate, true);
                }
            }
        }
    }

    protected void updateFluidState(Level world, BlockPos pos, BlockState state, float waterLevel) {
        boolean shouldHaveWater = waterLevel - pos.getY() > 0.5;
        boolean hasWater = WATER.fetch(state);
        if(shouldHaveWater != hasWater) {
            // 2: Send update to client
            // 4: Prevent render update
            // 16: Prevent neighbour reactions
            // 32: Prevent neighbour drops
            world.setBlock(pos, WATER.apply(state, shouldHaveWater), 2 + 4 + 16 + 32);
        }
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return (WATER.fetch(state) ? AgriFluidRegistry.getInstance().tank_water.get() : Fluids.EMPTY).defaultFluidState();
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
        return LADDER.fetch(state);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return this.getShape(state, world, pos, CollisionContext.empty());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter world, BlockPos pos) {
        return this.getCollisionShape(state, world, pos, CollisionContext.empty());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return this.getVisualShape(state, world, pos, CollisionContext.empty());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return getShape(state);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.getShape(state, world, pos, context);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.getShape(state, world, pos, context);
    }

    @Override
    public void addDrops(Consumer<ItemStack> dropAcceptor, BlockState state, TileEntityIrrigationTank tile, LootContext.Builder context) {
        if(LADDER.fetch(state)) {
            dropAcceptor.accept(new ItemStack(Items.LADDER, 1));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderType getRenderType() {
        return RenderType.cutout();
    }

    public enum Connection implements StringRepresentable {
        NONE(false, false,
                Block.box(0, 0, 0, 16, 16, 2),
                Block.box(0, 0, 0, 2, 16, 16)
        ),

        TANK(true, false,
                Shapes.empty(),
                Shapes.empty()
        ),

        CHANNEL(false, true,
                Stream.of(
                        Block.box(0, 0, 0, 16, 6, 2),
                        Block.box(0, 6, 0, 6, 10, 2),
                        Block.box(10, 6, 0, 16, 10, 2),
                        Block.box(0, 10, 0, 16, 16, 2)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
                Stream.of(
                        Block.box(0, 0, 0, 2, 6, 16),
                        Block.box(0, 6, 0, 2, 10, 6),
                        Block.box(0, 6, 10, 2, 10, 16),
                        Block.box(0, 10, 0, 2, 16, 16)
                ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()
        );

        private final boolean tank;
        private final boolean channel;

        private final VoxelShape north;
        private final VoxelShape east;
        private final VoxelShape south;
        private final VoxelShape west;

        Connection(boolean tank, boolean channel, VoxelShape north, VoxelShape west) {
            this.tank = tank;
            this.channel = channel;
            this.north = north;
            this.south = north.move(0, 0, 14 * Constants.UNIT);
            this.west = west;
            this.east = west.move(14 * Constants.UNIT, 0, 0);
        }

        public boolean isTank() {
            return this.tank;
        }

        public boolean isChannel() {
            return this.tank;
        }

        public VoxelShape getShape(Direction direction) {
            switch (direction) {
                case NORTH:
                    return this.north;
                case SOUTH:
                    return this.south;
                case EAST:
                    return this.east;
                case WEST:
                    return this.west;
            }
            return Shapes.empty();
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}
