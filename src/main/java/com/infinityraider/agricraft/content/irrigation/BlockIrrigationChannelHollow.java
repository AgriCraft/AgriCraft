package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.content.AgriItemRegistry;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.reference.Constants;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockIrrigationChannelHollow extends BlockIrrigationChannelAbstract {
    public BlockIrrigationChannelHollow() {
        super(Names.Blocks.CHANNEL_HOLLOW, false, Properties.of(Material.WOOD)
                .noOcclusion()
        );
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
        BlockIrrigationChannelAbstract.Valve valve = BlockIrrigationChannelAbstract.VALVE.fetch(state);
        if(valve.hasValve()) {
            boolean shouldBeClosed = Arrays.stream(Direction.values()).anyMatch(dir -> world.hasSignal(pos.relative(dir), dir));
            boolean isClosed = !valve.canTransfer();
            if(shouldBeClosed != isClosed) {
                world.setBlock(pos, BlockIrrigationChannelAbstract.VALVE.apply(state, valve.toggleValve()), 3);
                if(!world.isClientSide()) {
                    this.playValveSound(world, pos);
                }
            }
        }
    }

    @Nonnull
    @Override
    public ItemIrrigationChannelHollow asItem() {
        return AgriItemRegistry.getInstance().channel_hollow.get();
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
        return Shapes.block();
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
                Block.box(0, 0, 0, 16, 6, 16),
                Block.box(0, 10, 0, 16, 16, 16),
                Block.box(0, 6, 0, 6, 10, 6),
                Block.box(0, 6, 10, 6, 10, 16),
                Block.box(10, 6, 0, 16, 10, 6),
                Block.box(10, 6, 10, 16, 10, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        public static final VoxelShape NONE_NORTH = Block.box(6, 6, 0, 10, 10, 6);

        public static final VoxelShape NONE_SOUTH = NONE_NORTH.move(0, 0, 10*Constants.UNIT);

        public static final VoxelShape NONE_WEST = Block.box(0, 6, 6, 6, 10, 10);

        public static final VoxelShape NONE_EAST = NONE_WEST.move(10*Constants.UNIT, 0, 0);

        private ChannelShapes() {}
    }
}
