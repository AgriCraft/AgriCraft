package com.infinityraider.agricraft.content.irrigation;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.block.BlockBaseTile;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockSprinkler extends BlockBaseTile<TileEntitySprinkler> {
    // Properties
    public static final InfProperty<Boolean> ACTIVE = InfProperty.Creators.create("active", false);
    public static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder()
            .add(ACTIVE).build();

    // TileEntity factory
    private static final BiFunction<BlockPos, BlockState, TileEntitySprinkler> TILE_FACTORY = TileEntitySprinkler::new;

    // Shape
    private static final VoxelShape SHAPE = Stream.of(
            Block.box(5, 15, 5, 11, 21, 11),
            Block.box(8 - 0.74*Math.sqrt(2), 4, 8 - 0.74*Math.sqrt(2), 8 + 0.74*Math.sqrt(2), 15, 8 + 0.74*Math.sqrt(2))
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public BlockSprinkler() {
        super(Names.Blocks.SPRINKLER, Properties.of(Material.WOOD).noOcclusion().strength(2, 3));
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public BiFunction<BlockPos, BlockState, TileEntitySprinkler> getTileEntityFactory() {
        return TILE_FACTORY;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos().above();
        BlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof BlockIrrigationChannelAbstract) {
            return this.defaultBlockState();
        }
        return null;
    }

    @Override
    public final void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        BlockEntity tile = world.getBlockEntity(pos);
        if((!world.isClientSide())) {
            if(tile instanceof TileEntitySprinkler) {
                TileEntitySprinkler sprinkler = (TileEntitySprinkler) tile;
                BlockEntity above = world.getBlockEntity(pos.above());
                if(above instanceof TileEntityIrrigationChannel) {
                    // mimic channel wood
                    TileEntityIrrigationChannel channel = (TileEntityIrrigationChannel) above;
                    sprinkler.setMaterial(channel.getMaterial());
                    // return on success
                    return;
                }
            }
            // if this line is reached, something has gone wrong and a sprinkler should not exist on this location
            this.detachSprinkler(state, world, pos);
        }
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if(fromPos.below().equals(pos)) {
            this.detachSprinkler(state, world, pos);
        }
    }

    protected void detachSprinkler(BlockState state, Level world, BlockPos pos) {
        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        dropResources(state, world, pos, world.getBlockEntity(pos));
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
        return SHAPE;
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

    @Nonnull
    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public final List<ItemStack> getDrops(BlockState state, LootContext.Builder context) {
        return ImmutableList.of(new ItemStack(this));
    }
}
