package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.block.BlockBaseTile;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
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
    private static final BiFunction<BlockState, IBlockReader, TileEntitySprinkler> TILE_FACTORY = (s, w) -> new TileEntitySprinkler();

    // Shape
    private static final VoxelShape SHAPE = Stream.of(
            Block.makeCuboidShape(5, 15, 5, 11, 21, 11),
            Block.makeCuboidShape(8 - 0.74*Math.sqrt(2), 4, 8 - 0.74*Math.sqrt(2), 8 + 0.74*Math.sqrt(2), 15, 8 + 0.74*Math.sqrt(2))
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    public BlockSprinkler() {
        super(Names.Blocks.SPRINKLER, Properties.create(Material.WOOD));
    }

    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    @Override
    public BiFunction<BlockState, IBlockReader, TileEntitySprinkler> getTileEntityFactory() {
        return TILE_FACTORY;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getPos().up();
        BlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof BlockIrrigationChannelAbstract) {
            return this.getDefaultState();
        }
        return null;
    }

    @Override
    public final void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if((!world.isRemote())) {
            if(tile instanceof TileEntitySprinkler) {
                TileEntitySprinkler sprinkler = (TileEntitySprinkler) tile;
                TileEntity above = world.getTileEntity(pos.up());
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
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if(fromPos.down().equals(pos)) {
            this.detachSprinkler(state, world, pos);
        }
    }

    protected void detachSprinkler(BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
        spawnDrops(state, world, pos, world.getTileEntity(pos));
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
        return SHAPE;
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
}
