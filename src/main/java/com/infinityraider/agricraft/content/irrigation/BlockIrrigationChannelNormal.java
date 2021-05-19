package com.infinityraider.agricraft.content.irrigation;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.reference.Constants;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockIrrigationChannelNormal extends BlockIrrigationChannelAbstract {

    private static final Map<BlockState, VoxelShape> SHAPES = Maps.newHashMap();

    public static VoxelShape getShape(BlockState state) {
        return SHAPES.computeIfAbsent(state, aState -> Stream.concat(
                Stream.of(Shapes.BASE),
                Arrays.stream(Direction.values()).map(dir -> getConnection(dir).map(prop -> {
                    boolean connected = prop.fetch(aState);
                    switch (dir) {
                        case NORTH:
                            return connected ? Shapes.CHANNEL_NORTH : Shapes.NONE_NORTH;
                        case SOUTH:
                            return connected ? Shapes.CHANNEL_SOUTH : Shapes.NONE_SOUTH;
                        case WEST:
                            return connected ? Shapes.CHANNEL_WEST : Shapes.NONE_WEST;
                        case EAST:
                            return connected ? Shapes.CHANNEL_EAST : Shapes.NONE_EAST;
                        default:
                            return VoxelShapes.empty();
                    }
                })).filter(Optional::isPresent).map(Optional::get)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get());
    }

    public BlockIrrigationChannelNormal() {
        super(Names.Blocks.CHANNEL, Properties.create(Material.WOOD)
                .notSolid()
        );
    }

    @Nonnull
    @Override
    public ItemIrrigationChannel asItem() {
        return AgriCraft.instance.getModItemRegistry().channel;
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

    // VoxelShapes
    public static final class Shapes {
        public static final VoxelShape BASE = Stream.of(
                Block.makeCuboidShape(5, 5, 5, 11, 6, 11),
                Block.makeCuboidShape(5, 6, 5, 6, 10, 6),
                Block.makeCuboidShape(5, 6, 10, 6, 10, 11),
                Block.makeCuboidShape(10, 6, 5, 11, 10, 6),
                Block.makeCuboidShape(10, 6, 10, 11, 10, 11)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

        public static final VoxelShape CHANNEL_NORTH = Stream.of(
                Block.makeCuboidShape(5, 5, 0, 11, 6, 5),
                Block.makeCuboidShape(5, 6, 0, 6, 10, 5),
                Block.makeCuboidShape(10, 6, 0, 11, 10, 5)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

        public static final VoxelShape CHANNEL_SOUTH = CHANNEL_NORTH.withOffset(0, 0, 11*Constants.UNIT);

        public static final VoxelShape CHANNEL_WEST = Stream.of(
                Block.makeCuboidShape(0, 5, 5, 5, 6, 11),
                Block.makeCuboidShape(0, 6, 5, 5, 10, 6),
                Block.makeCuboidShape(0, 6, 10, 5, 10, 11)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

        public static final VoxelShape CHANNEL_EAST = CHANNEL_WEST.withOffset(11*Constants.UNIT, 0, 0);

        public static final VoxelShape NONE_NORTH = Block.makeCuboidShape(6, 6, 5, 10, 10, 6);

        public static final VoxelShape NONE_SOUTH = NONE_NORTH.withOffset(0, 0, 5*Constants.UNIT);

        public static final VoxelShape NONE_WEST = Block.makeCuboidShape(5, 6, 6, 6, 10, 10);

        public static final VoxelShape NONE_EAST = NONE_WEST.withOffset(5*Constants.UNIT, 0, 0);

        private Shapes() {}
    }
}
