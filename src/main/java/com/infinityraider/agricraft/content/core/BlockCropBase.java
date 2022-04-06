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
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
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
public abstract class BlockCropBase<T extends TileEntityCropBase> extends BlockBaseTile<T> implements IFluidLoggable, BonemealableBlock, IPlantable {
    // Properties
    public static final InfProperty<Boolean> PLANT = InfProperty.Creators.create("plant", false);
    public static final InfProperty<Integer> LIGHT = InfProperty.Creators.create("light", 0, 0, 16);

    public BlockCropBase(String name, Properties properties) {
        super(name, properties);
    }

    public Optional<IAgriCrop> getCrop(BlockGetter world, BlockPos pos) {
        return AgriApi.getCrop(world, pos);
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
    public final InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // fetch crop
        Optional<IAgriCrop> optional = this.getCrop(world, pos);
        if (!optional.isPresent()) {
            return InteractionResult.FAIL;
        }
        IAgriCrop crop = optional.get();
        // run pre logic
        if(crop.hasPlant()) {
            IAgriPlant plant = crop.getPlant();
            if(plant.isPlant()) {
                Optional<InteractionResult> result = plant.onRightClickPre(crop, player.getItemInHand(hand), player);
                if(result.isPresent()) {
                    return result.get();
                }
            }
        }
        // run default logic
        InteractionResult result = this.onCropRightClicked(world, pos, crop, player, hand, hit);
        // run post logic
        if(crop.hasPlant()) {
            IAgriPlant plant = crop.getPlant();
            if(plant.isPlant()) {
                Optional<InteractionResult> override = plant.onRightClickPost(crop, player.getItemInHand(hand), player);
                if(override.isPresent()) {
                    return override.get();
                }
            }
        }
        return result;
    }

    protected abstract InteractionResult onCropRightClicked(Level world, BlockPos pos, IAgriCrop crop, Player player, InteractionHand hand, BlockHitResult hit);

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if(fromPos.above().equals(pos)) {
            if(!state.canSurvive(world, pos)) {
                this.breakBlock(state, world, pos, true);
            }
        }
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState oldState, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        // Detect fluid state change
        if(oldState.getBlock() == newState.getBlock()) {
            Fluid oldFluid = this.getFluidState(oldState).getType();
            Fluid newFluid = this.getFluidState(newState).getType();
            if(oldFluid != newFluid && this.onFluidChanged(world, pos, newState, oldFluid, newFluid)) {
                return;
            }
        }
        // Call super
        super.onRemove(oldState, world, pos, newState, isMoving);
    }

    protected abstract boolean onFluidChanged(Level world, BlockPos pos, BlockState state, Fluid oldFluid, Fluid newFluid);

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockState current = world.getBlockState(pos);
        return current.getMaterial().isReplaceable() && AgriApi.getSoil(world, pos.below()).isPresent();
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.getStateForPlacement(context.getLevel(), context.getClickedPos());
    }

    @Nullable
    public BlockState getStateForPlacement(Level world, BlockPos pos) {
        BlockState state = this.defaultBlockState();
        if(state.canSurvive(world, pos)) {
            return this.fluidlog(state, world, pos);
        }
        return null;
    }

    public void breakBlock(BlockState state, Level world, BlockPos pos, boolean doDrops) {
        if(!world.isClientSide()) {
            if(doDrops) {
                dropResources(state, world, pos, world.getBlockEntity(pos));
            }
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
        this.getCrop(world, pos).ifPresent(IAgriCrop::applyGrowthTick);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void attack(BlockState state, Level world, BlockPos pos, Player player) {
        this.getCrop(world, pos).ifPresent(crop -> crop.breakCrop(player));
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        this.getCrop(world, pos).ifPresent(crop -> crop.getPlant().onEntityCollision(crop, entity));
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public boolean isSignalSource(BlockState state) {
        return PLANT.fetch(state);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return this.getDirectSignal(state, world, pos, side);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public boolean hasAnalogOutputSignal(BlockState state) {
        return PLANT.fetch(state);
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return this.getDirectSignal(state, world, pos, null);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
        return PLANT.fetch(state)
                ? this.getCrop(world, pos).map(crop -> crop.getPlant().getRedstonePower(crop)).orElse(0)
                : 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderType getRenderType() {
        return RenderType.cutout();
    }

    public void spawnItem(IAgriCrop crop, ItemStack stack) {
        Level world = crop.world();
        if(world != null) {
            this.spawnItem(world, crop.getPosition(), stack);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, Random rand) {
        this.getCrop(world, pos).ifPresent(crop -> {
            if(crop.hasPlant()) {
                crop.getPlant().spawnParticles(crop, rand);
            }
        });
    }

    /**
     * -------------------------
     * Vanilla BonemealableBlock methods
     * -------------------------
     */

    @Override
    public boolean isValidBonemealTarget(BlockGetter world, BlockPos pos, BlockState state, boolean isClient) {
        return this.getCrop(world, pos).map(crop -> crop.isFertile() && !crop.isFullyGrown()).orElse(true);
    }

    private static final ItemStack BONE_MEAL = new ItemStack(Items.BONE_MEAL);

    @Override
    public boolean isBonemealSuccess(Level world, Random rand, BlockPos pos, BlockState state) {
        return AgriApi.getFertilizer(BONE_MEAL).flatMap(fertilizer ->
                        this.getCrop(world, pos).map(crop ->
                                !crop.isFullyGrown()
                                        && crop.acceptsFertilizer(fertilizer)))
                .orElse(false);
    }

    @Override
    public void performBonemeal(ServerLevel world, Random rand, BlockPos pos, BlockState state) {
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
    public BlockState getPlant(BlockGetter world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if(world instanceof Level) {
            return this.getCrop(world, pos)
                    .flatMap(crop -> crop.getPlant().asBlockState(crop.getGrowthStage()))
                    .orElse(state);
        }
        return state;
    }
}
