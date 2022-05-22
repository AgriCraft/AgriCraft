package com.infinityraider.agricraft.content.world;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.world.IAgriGreenHouse;
import com.infinityraider.agricraft.content.AgriBlockRegistry;
import com.infinityraider.agricraft.content.core.BlockSeedAnalyzer;
import com.infinityraider.agricraft.handler.GreenHouseHandler;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.block.BlockBase;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
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

    public IAgriGreenHouse.State getState(BlockState state) {
        return STATE.fetch(state);
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public ItemGreenHouseMonitor asItem() {
        return AgriCraft.instance.getModItemRegistry().getGreenHouseMonitorItem();
    }

    public MutableComponent getFeedbackMessage(BlockState state) {
        return this.getFeedbackMessage(this.getState(state));
    }

    public MutableComponent getFeedbackMessage(IAgriGreenHouse.State state) {
        return this.asItem().getFeedbackMessage(state);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if(context.getClickedFace().getAxis() == Direction.Axis.Y) {
            // can not place vertically
            return null;
        }
        // apply orientation
        BlockState state = ORIENTATION.apply(this.defaultBlockState(), context.getHorizontalDirection());
        // apply greenhouse state
        if(state.canSurvive(context.getLevel(), context.getClickedPos())) {
            state = STATE.apply(state, AgriApi.getGreenHouse(context.getLevel(), context.getClickedPos())
                    .map(IAgriGreenHouse::getState)
                    .orElse(IAgriGreenHouse.State.REMOVED));
            // return the state
            return state;
        }
        return null;
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        Direction dir = ORIENTATION.fetch(state);
        BlockState solid = world.getBlockState(pos.relative(dir));
        BlockState air = world.getBlockState(pos.relative(dir.getOpposite()));
        return air.isAir() && solid.isFaceSturdy(world, pos.relative(dir), dir.getOpposite());
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
        super.neighborChanged(state, world, pos, block, fromPos, moving);
        Direction orientation = ORIENTATION.fetch(state);
        if(pos.relative(orientation).equals(fromPos)) {
            // solid block changed
            BlockState solid = world.getBlockState(fromPos);
            if(!solid.isFaceSturdy(world, fromPos, orientation.getOpposite())) {
                // if there no longer is a solid block behind the monitor, drop it
                if(!world.isClientSide()) {
                    Block.dropResources(state, world, pos);
                }
                world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        } else if(pos.relative(orientation.getOpposite()).equals(fromPos)) {
            // air block changed
            BlockState air = world.getBlockState(fromPos);
            if(!air.isAir()) {
                // if there no longer is air in front of the monitor, drop it
                if(!world.isClientSide()) {
                    Block.dropResources(state, world, pos);
                }
                world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            } else {
                // the air block has changed
                if(air.getBlock() == AgriBlockRegistry.getInstance().getGreenHouseAirBlock()) {
                    // greenhouse state change: update the state
                    world.setBlock(pos, STATE.apply(state, STATE.fetch(air)), 3);
                } else {
                    // check if a greenhouse still exists
                    boolean removed = GreenHouseHandler.getInstance().getGreenHouse(world, pos).map(gh -> {
                        // greenhouse is present but has been removed
                        if(gh.isRemoved()) {
                            // leave the block as air
                            return true;
                        }
                        // greenhouse is present and has not been removed; set the block back to greenhouse air
                        world.setBlock(fromPos, STATE.apply(AgriBlockRegistry.getInstance().getGreenHouseAirBlock().defaultBlockState(), gh.getState()), 3);
                        return false;
                    }).orElse(true);
                    // if the greenhouse has been removed, update the state
                    if(removed) {
                        world.setBlock(pos, STATE.apply(state, IAgriGreenHouse.State.REMOVED), 3);
                    }
                }
            }
        }
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide()) {
            if (hand == InteractionHand.MAIN_HAND) {
                if (player.isDiscrete()) {
                    GreenHouseHandler.getInstance().createGreenHouse(world, pos.relative(ORIENTATION.fetch(state).getOpposite()));
                }
                player.sendMessage(this.getFeedbackMessage(state), player.getUUID());
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState pState, LootContext.Builder builder) {
        return ImmutableList.of(new ItemStack(this));
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
