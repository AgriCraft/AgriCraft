package com.infinityraider.agricraft.content.core;

import com.agricraft.agricore.util.TypeHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.content.items.*;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneCarrierItem;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthResponse;
import com.infinityraider.agricraft.content.tools.ItemSeedBag;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.agricraft.util.CropHelper;
import com.infinityraider.infinitylib.block.BlockBaseTile;
import com.infinityraider.infinitylib.block.IFluidLoggable;
import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import com.infinityraider.infinitylib.reference.Constants;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockCrop extends BlockBaseTile<TileEntityCrop> implements IFluidLoggable, BonemealableBlock, IPlantable {
    /** Properties */
    protected static final InfProperty<CropStickVariant> VARIANT = InfProperty.Creators.create("stick_type", CropStickVariant.class, CropStickVariant.WOOD);
    protected static final InfProperty<CropState> STATE = InfProperty.Creators.create("crop_state", CropState.class, CropState.SINGLE_STICKS);
    protected static final InfProperty<Integer> LIGHT = InfProperty.Creators.create("light", 0, 0, 16);

    private static final InfPropertyConfiguration PROPERTIES = InfPropertyConfiguration.builder()
            .add(VARIANT)
            .add(STATE)
            .add(LIGHT)
            .fluidloggable()
            .build();

    /** Excluded classes for Item usage logic */
    private static final Class<?>[] ITEM_EXCLUDES = new Class[]{
            IAgriRakeItem.class,
            IAgriClipperItem.class,
            IAgriTrowelItem.class,
            ItemCropSticks.class,
            ItemSeedBag.class,
            ItemDebugger.class
    };

    /** TileEntity factory */
    private static final BiFunction<BlockPos, BlockState, TileEntityCrop> TILE_FACTORY = TileEntityCrop::new;

    /** Constructor */
    public BlockCrop() {
        super(Names.Blocks.CROP, Properties.of(Material.PLANT)
                .randomTicks()
                .noOcclusion()
                .lightLevel(LIGHT::fetch)
                .sound(SoundType.CROP)
        );
    }

    /**
     * --------------------------------
     * InfinityLib registration methods
     * --------------------------------
     */

    /** For TileEntity creation */
    @Override
    public BiFunction<BlockPos, BlockState, TileEntityCrop> getTileEntityFactory() {
        return TILE_FACTORY;
    }

    /** For BlockState definition */
    @Override
    protected InfPropertyConfiguration getPropertyConfiguration() {
        return PROPERTIES;
    }

    /** For render type registration */
    @Override
    @OnlyIn(Dist.CLIENT)
    public RenderType getRenderType() {
        return RenderType.cutout();
    }

    /**
     * ------------------------------------------
     * Block state and TileEntity utility methods
     * ------------------------------------------
     */

    /** TileEntity fetch utility method */
    public Optional<IAgriCrop> getCrop(BlockGetter world, BlockPos pos) {
        return AgriApi.getCrop(world, pos);
    }

    /** Checks if the block state has a plant or weeds */
    public boolean hasPlantOrWeeds(BlockState state) {
        return STATE.fetch(state).hasPlant();
    }

    /** Checks if the block state has crop sticks */
    public boolean hasCropSticks(BlockState state) {
        return STATE.fetch(state).hasSticks();
    }

    /** Checks if the block state has cross crop sticks */
    public boolean hasCrossSticks(BlockState state) {
        return STATE.fetch(state).isCross();
    }

    /** Gets the light level emitted by the block state */
    public int getLightLevel(BlockState state) {
        return LIGHT.fetch(state);
    }

    /** Fetches the crop stick variant if the state has crop sticks */
    public Optional<IAgriCropStickItem.Variant> getCropStickVariant(BlockState state) {
        if(this.hasCropSticks(state)) {
            return Optional.of(VARIANT.fetch(state));
        } else {
            return Optional.empty();
        }
    }

    /** BlockState for a plant only */
    public BlockState blockStatePlant() {
        return STATE.apply(this.defaultBlockState(), CropState.PLANT);
    }

    /** BlockState for crop sticks only */
    public BlockState blockStateCropSticks(CropStickVariant variant) {
        return VARIANT.apply(STATE.apply(this.defaultBlockState(), CropState.SINGLE_STICKS), variant);
    }

    /** Applies a plant to the block state */
    public BlockState applyPlant(BlockState state) {
        return STATE.fetch(state).addPlant(state);
    }

    /** Removes a plant from the block state */
    public BlockState removePlant(BlockState state) {
        return STATE.fetch(state).removePlant(state);
    }

    /** Sets the light level emitted by the blockstate */
    public BlockState setLightLevel(BlockState state, int level) {
        return LIGHT.apply(state, level);
    }

    /** Method to apply crop sticks to an existing crop */
    public InteractionResult applyCropSticks(Level world, BlockPos pos, BlockState state, @Nullable IAgriCropStickItem.Variant variant) {
        if(variant == null) {
            return InteractionResult.FAIL;
        }
        if(world.isClientSide()) {
            return InteractionResult.PASS;
        }
        BlockState newState = STATE.fetch(state).applyCropSticks(state, variant);
        if(newState == state) {
            return InteractionResult.FAIL;
        } else {
            world.setBlock(pos, newState, 3);
            if(STATE.fetch(state).hasSticks()) {
                variant.playCropStickSound(world, pos);
            } else {
                CropHelper.playPlantingSound(world, pos);
            }
            return InteractionResult.SUCCESS;
        }
    }

    /** Method to remove crop sticks from an existing crop */
    public InteractionResultHolder<IAgriCropStickItem.Variant> removeCropSticks(Level world, BlockPos pos, BlockState state) {
        if(world.isClientSide()) {
            return InteractionResultHolder.pass(VARIANT.fetch(state));
        }
        BlockState newState = STATE.fetch(state).addPlant(state);
        if(newState == state) {
            return InteractionResultHolder.fail(VARIANT.fetch(state));
        } else {
            world.setBlock(pos, newState, 3);
            if(STATE.fetch(state).isCross()) {
                VARIANT.fetch(state).playCropStickSound(world, pos);
            }
            return InteractionResultHolder.success(VARIANT.fetch(state));
        }
    }

    /** Method to apply a plant to an existing crop */
    public InteractionResult applyPlant(Level world, BlockPos pos, BlockState state) {
        if(world.isClientSide()) {
            return InteractionResult.PASS;
        }
        BlockState newState = this.applyPlant(state);
        if(newState == state) {
            return InteractionResult.FAIL;
        } else {
            world.setBlock(pos, newState, 3);
            return InteractionResult.SUCCESS;
        }
    }

    /** Method to remove a plant from an existing crop */
    public InteractionResult removePlant(Level world, BlockPos pos, BlockState state) {
        if(world.isClientSide()) {
            return InteractionResult.PASS;
        }
        BlockState newState = this.removePlant(state);
        if(newState == state) {
            return InteractionResult.FAIL;
        } else {
            world.setBlock(pos, newState, 3);
            return InteractionResult.SUCCESS;
        }
    }

    /** Method to set the light level of an existing crop */
    public InteractionResult setLightLevel(Level world, BlockPos pos, BlockState state, int level) {
        if(world.isClientSide()) {
            return InteractionResult.PASS;
        }
        BlockState newState = LIGHT.apply(state, level);
        if(newState == state) {
            return InteractionResult.FAIL;
        } else {
            world.setBlock(pos, newState, 3);
            return InteractionResult.SUCCESS;
        }
    }


    /**
     * -----------------------
     * Block Behaviour methods
     * -----------------------
     */

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
        BlockEntity tile = world.getBlockEntity(pos);
        if(tile instanceof TileEntityCrop) {
            Arrays.stream(Direction.values())
                    .filter(dir -> dir.getAxis().isHorizontal())
                    .filter(dir -> pos.relative(dir).equals(fromPos))
                    .forEach(dir -> ((TileEntityCrop) tile).onNeighbourChange(dir, fromPos, world.getBlockState(fromPos)));
        }
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

    protected boolean onFluidChanged(Level world, BlockPos pos, BlockState state, Fluid oldFluid, Fluid newFluid) {
        Optional<IAgriCrop> optCrop = this.getCrop(world, pos);
        boolean noMorePlant = optCrop.map(crop -> {
            if (!crop.hasPlant()) {
                return true;
            }
            IAgriGrowthResponse response = crop.getPlant().getGrowthRequirement(crop.getGrowthStage()).getFluidResponse(newFluid, crop.getStats().getStrength());
            if (response.killInstantly()) {
                response.onPlantKilled(crop);
                crop.removeGenome();
                return true;
            }
            return false;
        }).orElse(true);
        if(VARIANT.fetch(state).canExistInFluid(newFluid)) {
            // the crop sticks remain, regardless of what happened to the plant
            return false;
        } else {
            if(noMorePlant) {
                // no more crop sticks, no more plant, only fluid
                world.setBlock(pos, newFluid.defaultFluidState().createLegacyBlock(), 3);
                if(world instanceof ServerLevel) {
                    double x = pos.getX() + 0.5;
                    double y = pos.getY() + 0.5;
                    double z = pos.getZ() + 0.5;
                    for(int i = 0; i < 2; i++) {
                        world.addParticle(ParticleTypes.SMOKE,
                                x + 0.25*world.getRandom().nextDouble(), y, z + 0.25*world.getRandom().nextDouble(),
                                0, 1, 0);
                    }
                    world.playSound(null, x, y, z, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS,
                            0.2F + world.getRandom().nextFloat() * 0.2F, 0.9F + world.getRandom().nextFloat() * 0.05F);
                }
            } else {
                // no more crop sticks, but still plant, and fluid
                BlockState newState = this.defaultBlockState();
                newState = STATE.apply(newState, CropState.PLANT);
                newState = LIGHT.mimic(state, newState);
                newState = InfProperty.Defaults.fluidlogged().mimic(state, newState);
                world.setBlock(pos, newState, 3);
            }
            return true;
        }
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return AgriApi.getSoil(world, pos.below()).isPresent();
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack stack = context.getItemInHand();
        BlockState state = this.defaultBlockState();
        if(stack.getItem() instanceof IAgriCropStickItem) {
            // define crop stick type
            state = this.blockStateCropSticks(CropStickVariant.fromItem(stack));
        } else if (stack.getItem() instanceof IAgriSeedItem) {
            if(AgriCraft.instance.getConfig().allowPlantingOutsideCropSticks()) {
                // define plant type
                state = this.blockStatePlant();
            } else {
                // Not allowed to plant outside crop sticks
                return null;
            }
        }
        return this.adaptStateForPlacement(state, world, pos);
    }

    @Nullable
    public BlockState adaptStateForPlacement(BlockState state, Level world, BlockPos pos) {
        if(state.canSurvive(world, pos)) {
            return this.fluidlog(state, world, pos);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(world, pos, state, entity, stack);
        if(stack.getItem() instanceof IAgriGeneCarrierItem) {
            IAgriGeneCarrierItem seed = (IAgriGeneCarrierItem) stack.getItem();
            this.getCrop(world, pos).ifPresent(crop ->
                seed.getGenome(stack).map(genome -> crop.plantGenome(genome, entity)).map(result -> {
                    if (result) {
                        // consume item
                        if (entity == null || (entity instanceof Player && !((Player) entity).isCreative()) ) {
                            stack.shrink(1);
                        }
                    }
                    return result;
                })
            );
        }
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
    public final InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // fetch crop
        Optional<IAgriCrop> optional = this.getCrop(world, pos);
        if (!optional.isPresent()) {
            return InteractionResult.FAIL;
        }
        IAgriCrop crop = optional.get();
        // run plant pre logic
        if(crop.hasPlant()) {
            IAgriPlant plant = crop.getPlant();
            if(plant.isPlant()) {
                Optional<InteractionResult> result = plant.onRightClickPre(crop, player.getItemInHand(hand), player);
                if(result.isPresent()) {
                    return result.get();
                }
            }
        }
        // run block logic
        InteractionResult result = this.runRightClickLogic(world, pos, state, crop, player, hand, hit);
        // run plant post logic
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

    protected InteractionResult runRightClickLogic(Level world, BlockPos pos, BlockState state, IAgriCrop crop, Player player, InteractionHand hand, BlockHitResult hit) {
        // do nothing from off hand
        if(hand == InteractionHand.OFF_HAND) {
            return InteractionResult.PASS;
        }
        ItemStack heldItem = player.getItemInHand(hand);
        // harvesting (or de-cross-crop'ing)
        if (heldItem.isEmpty()) {
            if(crop.isCrossCrop()) {
                InteractionResultHolder<IAgriCropStickItem.Variant> result = this.removeCropSticks(world, pos, state);
                if (result.getResult() == InteractionResult.SUCCESS) {
                    if(!player.isCreative()) {
                        this.spawnItem(crop, result.getObject().createItemStack());
                    }
                    return InteractionResult.CONSUME;
                }
            } else {
                return crop.harvest(stack -> this.spawnItem(crop, stack), player);
            }
        }
        // Specific item interactions
        if (TypeHelper.isAnyType(heldItem.getItem(), ITEM_EXCLUDES)) {
            return InteractionResult.PASS;
        }
        // Fertilization
        if (AgriApi.getFertilizerAdapterizer().hasAdapter(heldItem)) {
            return AgriApi.getFertilizerAdapterizer().valueOf(heldItem).map(fertilizer -> {
                if(crop.acceptsFertilizer(fertilizer)) {
                    InteractionResult result = fertilizer.applyFertilizer(world, pos, crop, heldItem, world.getRandom(), player);
                    if(result == InteractionResult.CONSUME || result == InteractionResult.SUCCESS) {
                        crop.onApplyFertilizer(fertilizer, world.getRandom());
                    }
                    return result;
                } else {
                    return InteractionResult.CONSUME;
                }
            }).orElse(InteractionResult.PASS);
        }
        // Placement of crop sticks or creation of cross crop
        if(heldItem.getItem() instanceof IAgriCropStickItem) {
            InteractionResult result = this.applyCropSticks(world, pos, state, CropStickVariant.fromItem(heldItem));
            if (result == InteractionResult.SUCCESS) {
                if (!player.isCreative()) {
                    player.getItemInHand(hand).shrink(1);
                }
                return result;
            }
        }
        // Planting from seed
        final BlockState prevState = state;
        if(!crop.hasPlant()) {
            if (AgriApi.getGenomeAdapterizer().hasAdapter(heldItem)) {
                return AgriApi.getGenomeAdapterizer().valueOf(heldItem)
                        .map(seed -> {
                            if (crop.plantGenome(seed, player)) {
                                if (!player.isCreative()) {
                                    player.getItemInHand(hand).shrink(1);
                                }
                                // TODO: clean up
                                BlockState newState = STATE.apply(prevState, CropState.STICKS_PLANT);
                                newState = LIGHT.apply(newState, crop.getPlant().getBrightness(crop));
                                world.setBlock(pos, newState, 3);
                                player.swing(hand);
                                return InteractionResult.CONSUME;
                            } else {
                                return InteractionResult.PASS;
                            }
                        })
                        .orElse(InteractionResult.PASS);
            }
        }
        // Fall Back to harvesting
        return crop.harvest(stack -> this.spawnItem(crop, stack), player);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
        this.getCrop(world, pos).ifPresent(IAgriCrop::applyGrowthTick);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        double x = target.getLocation().x() - pos.getX();
        double z = target.getLocation().z() - pos.getZ();
        if(x >= 3*Constants.UNIT && x <= 13*Constants.UNIT && z >= 3*Constants.UNIT && z <= 13*Constants.UNIT) {
            // prioritize the plant
            ItemStack stack = this.getSeedStack(world, pos);
            return stack.isEmpty() ? this.getCropStickStack(state) : stack;
        } else {
            // prioritize the crop sticks
            ItemStack stack = this.getCropStickStack(state);
            return stack.isEmpty() ? this.getSeedStack(world, pos) : stack;
        }
    }

    public ItemStack getCropStickStack(BlockState state) {
        return this.getCropStickVariant(state)
                .map(IAgriCropStickItem.Variant::createItemStack)
                .orElse(ItemStack.EMPTY);
    }

    public ItemStack getSeedStack(BlockGetter world, BlockPos pos) {
        return AgriApi.getCrop(world, pos)
                .flatMap(IAgriCrop::getGenome)
                .map(IAgriGenome::toSeedStack)
                .orElse(ItemStack.EMPTY);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder context) {
        List<ItemStack> drops = Lists.newArrayList();
        BlockEntity tile = context.getParameter(LootContextParams.BLOCK_ENTITY);
        CropState cropState = STATE.fetch(state);
        CropStickVariant stickType = VARIANT.fetch(state);
        // add crop sticks
        boolean doStickDrops = (!AgriCraft.instance.getConfig().weedsDestroyCropSticks()) || (tile instanceof IAgriCrop) && (!((IAgriCrop) tile).hasWeeds());
        if(doStickDrops && cropState.hasSticks()) {
            drops.add(new ItemStack(stickType.getItem().asItem(), cropState.isCross() ? 2 : 1));
        }
        // add seed and drops
        if (tile instanceof IAgriCrop) {
            IAgriCrop crop = (IAgriCrop) tile;
            if (crop.hasPlant()) {
                // add plant fruits
                crop.getPlant().getHarvestProducts(drops::add, crop.getGrowthStage(), crop.getStats(), context.getLevel().getRandom());
                // drop the seed
                if(crop.getGrowthStage().canDropSeed()) {
                    crop.getGenome().map(IAgriGenome::toSeedStack).ifPresent(drops::add);
                }
            }
        } else {
            // The TileEntity no longer exists, log an error
            AgriCraft.instance.getLogger().error("Could not find IAgriCrop instance associated with crop sticks: " + context);
        }
        return drops;
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
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        return VARIANT.fetch(state).getSound();
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public boolean isSignalSource(BlockState state) {
        return STATE.fetch(state).hasPlant();
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
        return  STATE.fetch(state).hasPlant();
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return this.getDirectSignal(state, world, pos, null);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
        return STATE.fetch(state).hasPlant()
                ? this.getCrop(world, pos).map(crop -> crop.getPlant().getRedstonePower(crop)).orElse(0)
                : 0;
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

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        // override to prevent filling of creative tab
    }


    /**
     * ---------------
     * Hit box methods
     * --------------
     */

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
        CropState cropState = STATE.fetch(state);
        if(cropState.isCross()) {
            return HitBoxes.CROSS_STICKS;
        } else if(cropState.hasPlant()) {
            int height = this.getCrop(world, pos)
                    .map(crop -> Math.max(
                            crop.getPlant().getPlantHeight(crop.getGrowthStage()),
                            crop.getWeeds().getPlantHeight(crop.getWeedGrowthStage())))
                    .map(Double::intValue)
                    .orElse(0);
            if (cropState.hasSticks()) {
                return HitBoxes.getStickPlantShape(height);
            } else {
                return HitBoxes.getPlantShape(height);
            }
        } else {
            return HitBoxes.SINGLE_STICKS;
        }
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if(AgriCraft.instance.getConfig().cropSticksCollide()) {
            if(STATE.fetch(state).hasSticks()) {
                return STATE.fetch(state).isCross() ? HitBoxes.CROSS_STICKS : HitBoxes.SINGLE_STICKS;
            }
        }
        return Shapes.empty();
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.getShape(state, world, pos, context);
    }

    /**
     * ---------------------------------
     * Vanilla BonemealableBlock methods
     * ---------------------------------
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

    /**
     * -------------------------
     * Utility enums and classes
     * -------------------------
     */

    /** enum representing crop stick and plant state */
    protected enum CropState implements StringRepresentable {
        PLANT(true, false,
                // add crop sticks function
                (state, variant) -> {
                    state = STATE.apply(state, sticksPlant());
                    return VARIANT.apply(state, variant);
                },
                // remove crop sticks function
                UnaryOperator.identity(),
                // add plant function
                UnaryOperator.identity(),
                // remove plant function
                state -> Blocks.AIR.defaultBlockState()
        ),

        SINGLE_STICKS(false, true,
                // add crop sticks function
                (state, variant) -> {
                    if(VARIANT.fetch(state) == variant) {
                        return STATE.apply(state, doubleSticks());
                    } else {
                        return state;
                    }
                },
                // remove crop sticks function
                state -> Blocks.AIR.defaultBlockState(),
                // add plant function
                state -> STATE.apply(state, sticksPlant()),
                // remove plant function
                UnaryOperator.identity()
        ),

        DOUBLE_STICKS(false, true,
                // add crop sticks function
                (state, variant) -> state,
                // remove crop sticks function
                state -> STATE.apply(state, SINGLE_STICKS),
                // add plant function
                state -> STATE.apply(state, sticksPlant()),
                // remove plant function
                UnaryOperator.identity()
        ),

        STICKS_PLANT(true, true,
                // add crop sticks function
                (state, variant) -> state,
                // remove crop sticks function
                state -> STATE.apply(state, PLANT),
                // add plant function
                UnaryOperator.identity(),
                // remove plant function
                state -> STATE.apply(state, SINGLE_STICKS)
        );

        private final boolean plant;
        private final boolean sticks;

        private final BiFunction<BlockState, CropStickVariant, BlockState> addCropSticksFunction;
        private final UnaryOperator<BlockState> removeCropSticksFunction;
        private final UnaryOperator<BlockState> addPlantFunction;
        private final UnaryOperator<BlockState> removePlantFunction;

        CropState(boolean plant, boolean sticks,
                  BiFunction<BlockState, CropStickVariant, BlockState> addCropSticksFunction,
                  UnaryOperator<BlockState> removeCropSticksFunction,
                  UnaryOperator<BlockState> addPlantFunction,
                  UnaryOperator<BlockState> removePlantFunction) {
            this.plant = plant;
            this.sticks = sticks;
            this.addCropSticksFunction = addCropSticksFunction;
            this.removeCropSticksFunction = removeCropSticksFunction;
            this.addPlantFunction = addPlantFunction;
            this.removePlantFunction = removePlantFunction;
        }

        public boolean hasPlant() {
            return this.plant;
        }

        public boolean hasSticks() {
            return this.sticks;
        }

        public boolean isCross() {
            return this == DOUBLE_STICKS;
        }

        public BlockState applyCropSticks(BlockState state, IAgriCropStickItem.Variant variant) {
            if(variant instanceof CropStickVariant) {
                return this.addCropSticksFunction.apply(state, (CropStickVariant) variant);
            } else {
                AgriCraft.instance.getLogger().error("Attempted to apply unregistered crop stick variant to crop sticks: " + variant.getId());
                return state;
            }
        }

        public BlockState removeCropSticks(BlockState state) {
            return this.removeCropSticksFunction.apply(state);
        }

        public BlockState addPlant(BlockState state) {
            return this.addPlantFunction.apply(state);
        }

        public BlockState removePlant(BlockState state) {
            return this.removePlantFunction.apply(state);
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }

        // helper method to prevent illegal forward references in the constructors
        private static CropState sticksPlant() {
            return STICKS_PLANT;
        }

        // helper method to prevent illegal forward references in the constructors
        private static CropState doubleSticks() {
            return DOUBLE_STICKS;
        }
    }

    /** Inner class for voxel shapes */
    protected static class HitBoxes {
        public static final VoxelShape SINGLE_STICKS = Stream.of(
                Block.box(2, -3, 2, 3, 14, 3),
                Block.box(13, -3, 2, 14, 14, 3),
                Block.box(2, -3, 13, 3, 14, 14),
                Block.box(13, -3, 13, 14, 14, 14)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        public static final VoxelShape CROSS_STICKS = Stream.of(
                SINGLE_STICKS,
                Block.box(0, 11, 2, 16, 12, 3),
                Block.box(0, 11, 13, 16, 12, 14),
                Block.box(2, 11, 0, 3, 12, 16),
                Block.box(13, 11, 0, 14, 12, 16)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        private static final Map<Integer, VoxelShape> PLANTS = Maps.newHashMap();

        public static final Map<Integer, VoxelShape> STICK_PLANTS = Maps.newHashMap();

        public static VoxelShape getPlantShape(int height) {
            return PLANTS.computeIfAbsent(height, h -> Optional.of(Block.box(3, 0, 3, 13, h, 13)).get());
        }

        public static VoxelShape getStickPlantShape(int height) {
            return STICK_PLANTS.computeIfAbsent(height, h -> Stream.of(
                    SINGLE_STICKS,
                   getPlantShape(height)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get());
        }
    }
}
