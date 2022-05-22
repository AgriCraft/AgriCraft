package com.infinityraider.agricraft.content.world;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.world.IAgriGreenHouse;
import com.infinityraider.agricraft.content.core.BlockSeedAnalyzer;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockGreenHouseMonitor extends BlockBase {
    public static InfProperty<IAgriGreenHouse.State> STATE = BlockGreenHouseAir.STATE;
    public static InfProperty<Direction> ORIENTATION = BlockSeedAnalyzer.ORIENTATION;

    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder().add(STATE).add(ORIENTATION).build();

    public BlockGreenHouseMonitor() {
        super(Names.Blocks.GREENHOUSE_MONITOR, Properties.of(Material.GLASS)
                .noCollission()
                .noCollission()
                .instabreak()
        );
    }

    public static BlockState withState(BlockState monitor, IAgriGreenHouse.State state) {
        if(monitor.hasProperty(STATE.getProperty())) {
            return STATE.apply(monitor, state);
        }
        return monitor;
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public Item asItem() {
        return AgriApi.getAgriContent().getItems().getGreenHouseMonitorItem();
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if(context.getClickedFace().getAxis() == Direction.Axis.Y) {
            // can not place vertically
            return null;
        }
        // fetch default state
        BlockState state = this.defaultBlockState();
        if(state.canSurvive(context.getLevel(), context.getClickedPos())) {
            // apply greenhouse state
            state = STATE.apply(state, AgriApi.getGreenHouse(context.getLevel(), context.getClickedPos())
                    .map(IAgriGreenHouse::getState)
                    .orElse(IAgriGreenHouse.State.REMOVED));
            // apply orientation
            state = ORIENTATION.apply(state, context.getHorizontalDirection());
            // return the state
            return state;
        }
        return null;
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
        return Shapes.getShape(ORIENTATION.fetch(state));
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
    @OnlyIn(Dist.CLIENT)
    public RenderType getRenderType() {
        return RenderType.cutout();
    }

    private static class Shapes {
        private static final VoxelShape NORTH = Stream.of(
                // thermometer
                Block.box(2, 0, 0, 4, 1, 1),
                Block.box(1, 1, 0, 5, 2, 1),
                Block.box(0, 2, 0, 6, 4, 1),
                Block.box(1, 4, 0, 5, 15, 1),
                // hygrometer
                Block.box(9, 0, 0, 14, 1, 1),
                Block.box(8, 1, 0, 15, 2, 1),
                Block.box(7, 2, 0, 16, 6, 1),
                Block.box(8, 6, 0, 15, 8, 1),
                Block.box(9, 8, 0, 14, 11, 1),
                Block.box(10, 11, 0, 13, 15, 1)
                ).reduce((v1, v2) -> net.minecraft.world.phys.shapes.Shapes.join(v1, v2, BooleanOp.OR)).get().optimize();

        private static final VoxelShape EAST = Stream.of(
                // thermometer
                Block.box(15, 0, 2, 16, 1, 4),
                Block.box(15, 1, 1, 16, 2, 5),
                Block.box(15, 2, 0, 16, 4, 6),
                Block.box(15, 4, 1, 16, 15, 5),
                // hygrometer
                Block.box(15, 0, 9, 16, 1, 14),
                Block.box(15, 1, 8, 16, 2, 15),
                Block.box(15, 2, 7, 16, 6, 16),
                Block.box(15, 6, 8, 16, 8, 15),
                Block.box(15, 8, 9, 16, 11, 14),
                Block.box(15, 11, 10, 16, 15, 13)
        ).reduce((v1, v2) -> net.minecraft.world.phys.shapes.Shapes.join(v1, v2, BooleanOp.OR)).get().optimize();

        private static final VoxelShape SOUTH = Stream.of(
                // thermometer
                Block.box(12, 0, 15, 14, 1, 16),
                Block.box(11, 1, 15, 15, 2, 16),
                Block.box(10, 2, 15, 16, 4, 16),
                Block.box(11, 4, 15, 15, 15, 16),
                // hygrometer
                Block.box(2, 0, 15, 7, 1, 16),
                Block.box(1, 1, 15, 8, 2, 16),
                Block.box(0, 2, 15, 9, 6, 16),
                Block.box(1, 6, 15, 8, 8, 16),
                Block.box(2, 8, 15, 7, 11, 16),
                Block.box(3, 11, 15, 6, 15, 16)
        ).reduce((v1, v2) -> net.minecraft.world.phys.shapes.Shapes.join(v1, v2, BooleanOp.OR)).get().optimize();

        private static final VoxelShape WEST = Stream.of(
                // thermometer
                Block.box(0, 0, 12, 1, 1, 14),
                Block.box(0, 1, 11, 1, 2, 15),
                Block.box(0, 2, 10, 1, 4, 16),
                Block.box(0, 4, 11, 1, 15, 15),
                // hygrometer
                Block.box(0, 0, 2, 1, 1, 7),
                Block.box(0, 1, 1, 1, 2, 8),
                Block.box(0, 2, 0, 1, 6, 9),
                Block.box(0, 6, 1, 1, 8, 8),
                Block.box(0, 8, 2, 1, 11, 7),
                Block.box(0, 11, 3, 1, 15, 6)
        ).reduce((v1, v2) -> net.minecraft.world.phys.shapes.Shapes.join(v1, v2, BooleanOp.OR)).get().optimize();

        private static final VoxelShape[] SHAPES = {NORTH, SOUTH, WEST, EAST};

        public static VoxelShape getShape(Direction dir) {
            if(dir.getAxis().isVertical()) {
                return net.minecraft.world.phys.shapes.Shapes.empty();
            }
            return SHAPES[dir.ordinal() - 2];
        }
    }
}
