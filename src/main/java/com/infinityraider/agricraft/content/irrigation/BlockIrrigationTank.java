package com.infinityraider.agricraft.content.irrigation;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.block.BlockDynamicTexture;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import com.infinityraider.infinitylib.reference.Constants;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

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

    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder()
            .add(NORTH).add(EAST).add(SOUTH).add(WEST).add(DOWN)
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
    private static final BiFunction<BlockState, IBlockReader, TileEntityIrrigationTank> TILE_FACTORY = (s, w) -> new TileEntityIrrigationTank();

    // VoxelShapes
    public static final VoxelShape SHAPE_DOWN = Block.makeCuboidShape(0, 0, 0, 16, 2, 16);

    private static final Map<BlockState, VoxelShape> SHAPES = Maps.newConcurrentMap();

    public static VoxelShape getShape(BlockState state) {
        if (!(state.getBlock() instanceof BlockIrrigationTank)) {
            return VoxelShapes.empty();
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
                        Stream.of(DOWN.fetch(aState) ? VoxelShapes.empty() : SHAPE_DOWN)
                ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.fullCube()));
    }

    public BlockIrrigationTank() {
        super(Names.Blocks.TANK, Properties.create(Material.WOOD)
                .notSolid()
        );
    }

    @Override
    public ItemIrrigationTank asItem() {
        return AgriCraft.instance.getModItemRegistry().tank;
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public BiFunction<BlockState, IBlockReader, TileEntityIrrigationTank> getTileEntityFactory() {
        return TILE_FACTORY;
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
            ItemStack stack = player.getHeldItem(hand);
            if(stack.getItem() instanceof BucketItem) {
                BucketItem bucket = (BucketItem) stack.getItem();
                Fluid fluid = bucket.getFluid();
                if(fluid == Fluids.WATER) {
                    // try to fill from a bucket
                    TileEntity tile = world.getTileEntity(pos);
                    if(tile instanceof TileEntityIrrigationTank) {
                        TileEntityIrrigationTank tank = (TileEntityIrrigationTank) tile;
                        if(tank.pushWater(1000, false) == 1000) {
                            tank.pushWater(1000, true);
                            if(!player.isCreative()) {
                                player.setHeldItem(hand, new ItemStack(Items.BUCKET));
                            }
                            return ActionResultType.SUCCESS;
                        }
                    }
                } else if(fluid == Fluids.EMPTY) {
                    // try to drain to a bucket
                    TileEntity tile = world.getTileEntity(pos);
                    if(tile instanceof TileEntityIrrigationTank) {
                        TileEntityIrrigationTank tank = (TileEntityIrrigationTank) tile;
                        if(tank.drainWater(1000, false) == 1000) {
                            tank.drainWater(1000, true);
                            player.setHeldItem(hand, new ItemStack(Items.WATER_BUCKET));
                            return ActionResultType.SUCCESS;
                        }
                    }
                }
            }
        return ActionResultType.FAIL;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
                                ItemStack stack, @Nullable TileEntity tile) {
        if(tile instanceof TileEntityIrrigationTank) {
            ((TileEntityIrrigationTank) tile).checkAndFormMultiBlock();
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updatePostPlacement(BlockState ownState, Direction dir, BlockState otherState, IWorld world, BlockPos pos, BlockPos otherPos) {
        return getConnection(dir).map(prop -> {
            if(prop.fetch(ownState).isTank()) {
                // tank logic is handled from within the tile entity
                return ownState;
            }
            TileEntity ownTile = world.getTileEntity(pos);
            TileEntity otherTile = world.getTileEntity(otherPos);
            if(ownTile instanceof TileEntityIrrigationTank && otherTile instanceof TileEntityIrrigationChannel) {
                TileEntityIrrigationChannel channel = (TileEntityIrrigationChannel) otherTile;
                if (channel.canConnect((TileEntityIrrigationTank) ownTile)) {
                    return prop.apply(ownState, Connection.CHANNEL);
                }
            }
            return prop.apply(ownState, Connection.NONE);
        }).orElse(super.updatePostPlacement(ownState, dir, otherState, world, pos, otherPos));
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityIrrigationTank) {
            TileEntityIrrigationTank tank = (TileEntityIrrigationTank) tile;
            tank.onNeighbourChanged(fromPos);
            tank.onNeighbourUpdate(fromPos);
        }
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
    }

    @Override
    public void fillWithRain(World world, BlockPos pos) {
        int rate = AgriCraft.instance.getConfig().rainFillRate();
        if(rate > 0) {
            TileEntity tile = world.getTileEntity(pos);
            if(tile instanceof TileEntityIrrigationTank) {
                TileEntityIrrigationTank tank = (TileEntityIrrigationTank) tile;
                tank.pushWater(rate, true);
            }
        }
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getRenderShape(BlockState state, IBlockReader world, BlockPos pos) {
        return this.getShape(state, world, pos, ISelectionContext.dummy());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos) {
        return this.getCollisionShape(state, world, pos, ISelectionContext.dummy());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getRaytraceShape(BlockState state, IBlockReader world, BlockPos pos) {
        return this.getRayTraceShape(state, world, pos, ISelectionContext.dummy());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return getShape(state);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return this.getShape(state, world, pos, context);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getRayTraceShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return this.getShape(state, world, pos, context);
    }

    @Override
    public void addDrops(Consumer<ItemStack> dropAcceptor, BlockState state, TileEntityIrrigationTank tile, LootContext.Builder context) {
    }

    public enum Connection implements IStringSerializable {
        NONE(false, false,
                Block.makeCuboidShape(0, 0, 0, 16, 16, 2),
                Block.makeCuboidShape(0, 0, 0, 2, 16, 16)
        ),

        TANK(true, false,
                VoxelShapes.empty(),
                VoxelShapes.empty()
        ),

        CHANNEL(false, true,
                Stream.of(
                        Block.makeCuboidShape(0, 0, 0, 16, 6, 2),
                        Block.makeCuboidShape(0, 6, 0, 6, 10, 2),
                        Block.makeCuboidShape(10, 6, 0, 16, 10, 2),
                        Block.makeCuboidShape(0, 10, 0, 16, 16, 2)
                ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get(),
                Stream.of(
                        Block.makeCuboidShape(0, 0, 0, 2, 6, 16),
                        Block.makeCuboidShape(0, 6, 0, 2, 10, 6),
                        Block.makeCuboidShape(0, 6, 10, 2, 10, 16),
                        Block.makeCuboidShape(0, 10, 0, 2, 16, 16)
                ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get()
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
            this.south = north.withOffset(0, 0, 14 * Constants.UNIT);
            this.west = west;
            this.east = west.withOffset(14 * Constants.UNIT, 0, 0);
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
            return VoxelShapes.empty();
        }

        @Override
        public String getString() {
            return this.name().toLowerCase();
        }
    }
}
