package com.infinityraider.agricraft.content.irrigation;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.content.AgriItemRegistry;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.reference.Constants;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

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
                Stream.of(VALVE.fetch(state).hasValve() ? ChannelShapes.BASE_VALVE : ChannelShapes.BASE),
                Arrays.stream(Direction.values()).map(dir -> getConnection(dir).map(prop -> {
                    boolean connected = prop.fetch(aState);
                    switch (dir) {
                        case NORTH:
                            return connected ? ChannelShapes.CHANNEL_NORTH : ChannelShapes.NONE_NORTH;
                        case SOUTH:
                            return connected ? ChannelShapes.CHANNEL_SOUTH : ChannelShapes.NONE_SOUTH;
                        case WEST:
                            return connected ? ChannelShapes.CHANNEL_WEST : ChannelShapes.NONE_WEST;
                        case EAST:
                            return connected ? ChannelShapes.CHANNEL_EAST : ChannelShapes.NONE_EAST;
                        default:
                            return Shapes.empty();
                    }
                })).filter(Optional::isPresent).map(Optional::get)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get());
    }

    public BlockIrrigationChannelNormal() {
        super(Names.Blocks.CHANNEL, true, Properties.of(Material.WOOD)
                .noOcclusion()
        );
    }

    @Nonnull
    @Override
    public ItemIrrigationChannel asItem() {
        return AgriItemRegistry.getInstance().channel.get();
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockIrrigationChannelAbstract.Valve valve = BlockIrrigationChannelAbstract.VALVE.fetch(state);
        if(valve.hasValve()) {
            world.setBlock(pos, BlockIrrigationChannelAbstract.VALVE.apply(state, valve.toggleValve()), 3);
            if(world.isClientSide()) {
                BlockEntity tile = world.getBlockEntity(pos);
                if (tile instanceof TileEntityIrrigationChannel) {
                    TileEntityIrrigationChannel channel = (TileEntityIrrigationChannel) tile;
                    channel.setValveState(BlockIrrigationChannelAbstract.VALVE.fetch(state).canTransfer()
                            ? TileEntityIrrigationChannel.ValveState.CLOSING
                            : TileEntityIrrigationChannel.ValveState.OPENING
                    );
                }
            } else {
                this.playValveSound(world, pos);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
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

    // VoxelShapes
    public static final class ChannelShapes {
        public static final VoxelShape BASE = Stream.of(
                Block.box(5, 5, 5, 11, 6, 11),
                Block.box(5, 6, 5, 6, 10, 6),
                Block.box(5, 6, 10, 6, 10, 11),
                Block.box(10, 6, 5, 11, 10, 6),
                Block.box(10, 6, 10, 11, 10, 11)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        public static final VoxelShape BASE_VALVE = Stream.of(
                BASE,
                Block.box(5, 10, 5, 11, 16, 11)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        public static final VoxelShape CHANNEL_NORTH = Stream.of(
                Block.box(5, 5, 0, 11, 6, 5),
                Block.box(5, 6, 0, 6, 10, 5),
                Block.box(10, 6, 0, 11, 10, 5)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        public static final VoxelShape CHANNEL_SOUTH = CHANNEL_NORTH.move(0, 0, 11*Constants.UNIT);

        public static final VoxelShape CHANNEL_WEST = Stream.of(
                Block.box(0, 5, 5, 5, 6, 11),
                Block.box(0, 6, 5, 5, 10, 6),
                Block.box(0, 6, 10, 5, 10, 11)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        public static final VoxelShape CHANNEL_EAST = CHANNEL_WEST.move(11*Constants.UNIT, 0, 0);

        public static final VoxelShape NONE_NORTH = Block.box(6, 6, 5, 10, 10, 6);

        public static final VoxelShape NONE_SOUTH = NONE_NORTH.move(0, 0, 5*Constants.UNIT);

        public static final VoxelShape NONE_WEST = Block.box(5, 6, 6, 6, 10, 10);

        public static final VoxelShape NONE_EAST = NONE_WEST.move(5*Constants.UNIT, 0, 0);

        private ChannelShapes() {}
    }
}
