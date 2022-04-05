package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.infinitylib.block.BlockBaseTile;
import com.infinityraider.infinitylib.block.IFluidLoggable;
import com.infinityraider.infinitylib.block.property.InfProperty;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BlockCropBase<T extends TileEntityCropBase> extends BlockBaseTile<T> implements IFluidLoggable, IGrowable, IPlantable {
    // Properties
    public static final InfProperty<Boolean> PLANT = InfProperty.Creators.create("plant", false);
    public static final InfProperty<Integer> LIGHT = InfProperty.Creators.create("light", 0, 0, 16);

    public BlockCropBase(String name, Properties properties) {
        super(name, properties);
    }

    public Optional<IAgriCrop> getCrop(IBlockReader world, BlockPos pos) {
        return AgriApi.getCrop(world, pos);
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
    public final InteractionResult onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        // fetch crop
        Optional<IAgriCrop> optional = this.getCrop(world, pos);
        if (!optional.isPresent()) {
            return ActionResultType.FAIL;
        }
        IAgriCrop crop = optional.get();
        // run pre logic
        if(crop.hasPlant()) {
            IAgriPlant plant = crop.getPlant();
            if(plant.isPlant()) {
                Optional<ActionResultType> result = plant.onRightClickPre(crop, player.getHeldItem(hand), player);
                if(result.isPresent()) {
                    return result.get();
                }
            }
        }
        // run default logic
        ActionResultType result = this.onCropRightClicked(world, pos, crop, player, hand, hit);
        // run post logic
        if(crop.hasPlant()) {
            IAgriPlant plant = crop.getPlant();
            if(plant.isPlant()) {
                Optional<ActionResultType> override = plant.onRightClickPost(crop, player.getHeldItem(hand), player);
                if(override.isPresent()) {
                    return override.get();
                }
            }
        }
        return result;
    }

    protected abstract ActionResultType onCropRightClicked(World world, BlockPos pos, IAgriCrop crop, PlayerEntity player, Hand hand, BlockRayTraceResult hit);

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if(fromPos.up().equals(pos)) {
            if(!state.isValidPosition(world, pos)) {
                this.breakBlock(state, world, pos, true);
            }
        }
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void onReplaced(BlockState oldState, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        // Detect fluid state change
        if(oldState.getBlock() == newState.getBlock()) {
            Fluid oldFluid = this.getFluidState(oldState).getFluid();
            Fluid newFluid = this.getFluidState(newState).getFluid();
            if(oldFluid != newFluid && this.onFluidChanged(world, pos, newState, oldFluid, newFluid)) {
                return;
            }
        }
        // Call super
        super.onReplaced(oldState, world, pos, newState, isMoving);
    }

    protected abstract boolean onFluidChanged(World world, BlockPos pos, BlockState state, Fluid oldFluid, Fluid newFluid);

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
        BlockState current = world.getBlockState(pos);
        return current.getMaterial().isReplaceable() && AgriApi.getSoil(world, pos.down()).isPresent();
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getStateForPlacement(context.getWorld(), context.getPos());
    }

    @Nullable
    public BlockState getStateForPlacement(Level world, BlockPos pos) {
        BlockState state = this.getDefaultState();
        if(state.isValidPosition(world, pos)) {
            return this.fluidlog(state, world, pos);
        }
        return null;
    }

    public void breakBlock(BlockState state, World world, BlockPos pos, boolean doDrops) {
        if(!world.isRemote()) {
            if(doDrops) {
                spawnDrops(state, world, pos, world.getTileEntity(pos));
            }
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.getCrop(world, pos).ifPresent(IAgriCrop::applyGrowthTick);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        this.getCrop(world, pos).ifPresent(crop -> crop.breakCrop(player));
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        this.getCrop(world, pos).ifPresent(crop -> crop.getPlant().onEntityCollision(crop, entity));
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public boolean canProvidePower(BlockState state) {
        return PLANT.fetch(state);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return this.getStrongPower(state, world, pos, side);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public int getStrongPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return PLANT.fetch(state)
                ? this.getCrop(world, pos).map(crop -> crop.getPlant().getRedstonePower(crop)).orElse(0)
                : 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderType getRenderType() {
        return RenderType.getCutout();
    }

    public void spawnItem(IAgriCrop crop, ItemStack stack) {
        World world = crop.world();
        if(world != null) {
            this.spawnItem(world, crop.getPosition(), stack);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        this.getCrop(world, pos).ifPresent(crop -> {
            if(crop.hasPlant()) {
                crop.getPlant().spawnParticles(crop, rand);
            }
        });
    }

    /**
     * -------------------------
     * Vanilla IGrowable methods
     * -------------------------
     */

    @Override
    public boolean canGrow(IBlockReader world, BlockPos pos, BlockState state, boolean isClient) {
        return this.getCrop(world, pos).map(crop -> crop.isFertile() && !crop.isFullyGrown()).orElse(true);
    }

    private static final ItemStack BONE_MEAL = new ItemStack(Items.BONE_MEAL);

    @Override
    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, BlockState state) {
        return AgriApi.getFertilizer(BONE_MEAL).flatMap(fertilizer ->
                        this.getCrop(world, pos).map(crop ->
                                !crop.isFullyGrown()
                                        && crop.acceptsFertilizer(fertilizer)))
                .orElse(false);
    }

    @Override
    public void grow(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
        AgriApi.getFertilizer(BONE_MEAL).ifPresent(fertilizer ->
                this.getCrop(world, pos).ifPresent(crop ->
                        fertilizer.applyFertilizer(world, pos, crop, BONE_MEAL, rand, null)));
    }

    /**
     * ------------------------
     * Forge IPlantable methods
     * ------------------------
     */

    @Override
    public BlockState getPlant(IBlockReader world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if(world instanceof World) {
            return this.getCrop(world, pos)
                    .flatMap(crop -> crop.getPlant().asBlockState(crop.getGrowthStage()))
                    .orElse(state);
        }
        return state;
    }
}
