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
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockIrrigationChannelHollow extends BlockIrrigationChannelAbstract {

    private static final Map<BlockState, VoxelShape> SHAPES = Maps.newHashMap();

    public static VoxelShape getShape(BlockState state) {
        return SHAPES.computeIfAbsent(state, aState -> Stream.concat(
                Stream.of(Shapes.BASE),
                Arrays.stream(Direction.values()).map(dir -> getConnection(dir).map(prop -> {
                    if(!prop.fetch(aState)) {
                        switch (dir) {
                            case NORTH:
                                return Shapes.NONE_NORTH;
                            case SOUTH:
                                return Shapes.NONE_SOUTH;
                            case WEST:
                                return Shapes.NONE_WEST;
                            case EAST:
                                return Shapes.NONE_EAST;
                        }
                    }
                    return VoxelShapes.empty();
                })).filter(Optional::isPresent).map(Optional::get)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get());
    }

    public BlockIrrigationChannelHollow() {
        super(Names.Blocks.CHANNEL_HOLLOW, false, Properties.create(Material.WOOD)
                .notSolid()
        );
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
        BlockIrrigationChannelAbstract.Valve valve = BlockIrrigationChannelAbstract.VALVE.fetch(state);
        if(valve.hasValve()) {
            boolean shouldBeClosed = Arrays.stream(Direction.values()).anyMatch(dir -> world.isSidePowered(pos.offset(dir), dir));
            boolean isClosed = !valve.canTransfer();
            if(shouldBeClosed != isClosed) {
                world.setBlockState(pos, BlockIrrigationChannelAbstract.VALVE.apply(state, valve.toggleValve()));
                if(!world.isRemote()) {
                    this.playValveSound(world, pos);
                }
            }
        }
    }

    @Nonnull
    @Override
    public ItemIrrigationChannelHollow asItem() {
        return AgriCraft.instance.getModItemRegistry().channel_hollow;
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
                Block.makeCuboidShape(0, 0, 0, 16, 6, 16),
                Block.makeCuboidShape(0, 10, 0, 16, 16, 16),
                Block.makeCuboidShape(0, 6, 0, 6, 10, 6),
                Block.makeCuboidShape(0, 6, 10, 6, 10, 16),
                Block.makeCuboidShape(10, 6, 0, 16, 10, 6),
                Block.makeCuboidShape(10, 6, 10, 16, 10, 16)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

        public static final VoxelShape NONE_NORTH = Block.makeCuboidShape(6, 6, 0, 10, 10, 6);

        public static final VoxelShape NONE_SOUTH = NONE_NORTH.withOffset(0, 0, 10*Constants.UNIT);

        public static final VoxelShape NONE_WEST = Block.makeCuboidShape(0, 6, 6, 6, 10, 10);

        public static final VoxelShape NONE_EAST = NONE_WEST.withOffset(10*Constants.UNIT, 0, 0);

        private Shapes() {}
    }
}
