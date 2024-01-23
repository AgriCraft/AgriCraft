package com.agricraft.agricraft.common.block;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.config.CoreConfig;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.client.ClientUtil;
import com.agricraft.agricraft.common.block.entity.CropBlockEntity;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.agricraft.agricraft.common.item.CropSticksItem;
import com.agricraft.agricraft.common.registry.ModBlocks;
import com.agricraft.agricraft.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class CropBlock extends Block implements EntityBlock, BonemealableBlock, BucketPickup, LiquidBlockContainer {

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
	public static final EnumProperty<CropStickVariant> STICK_VARIANT = EnumProperty.create("variant", CropStickVariant.class, CropStickVariant.values());
	public static final EnumProperty<CropState> CROP_STATE = EnumProperty.create("crop", CropState.class, CropState.values());

	public CropBlock() {
		super(Properties.of()
				.mapColor(MapColor.PLANT)
				.pushReaction(PushReaction.DESTROY)
				.isRedstoneConductor((state, getter, pos) -> false)
				.randomTicks()
				.noOcclusion()
				.forceSolidOff()
				.noParticlesOnBreak()
				.lightLevel(BlockStateBase::getLightEmission)
				.sound(SoundType.CROP));
		this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false)
				.setValue(STICK_VARIANT, CropStickVariant.WOODEN)
				.setValue(CROP_STATE, CropState.SINGLE_STICKS));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.WATERLOGGED);
		builder.add(STICK_VARIANT);
		builder.add(CROP_STATE);
		// TODO: @Ketheroth add lavalogged property
	}

	@Override
	@NotNull
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		CropState cropState = state.getValue(CROP_STATE);
		if (cropState == CropState.DOUBLE_STICKS) {
			return CROSS_STICKS;
		}
		if (!cropState.hasPlant()) {
			return SINGLE_STICKS;
		}
		if (level.getBlockEntity(pos) instanceof CropBlockEntity cbe) {
			// shape is dependant of the plant
			return cropState.hasSticks() ? Shapes.join(SINGLE_STICKS, cbe.getShape(), BooleanOp.OR) : cbe.getShape();
		}
		return super.getShape(state, level, pos, context);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		CropState cropState = state.getValue(CROP_STATE);
		if (CoreConfig.cropSticksCollide) {
			if (cropState.hasSticks()) {
				return cropState == CropState.DOUBLE_STICKS ? CROSS_STICKS : SINGLE_STICKS;
			}
		}
		return Shapes.empty();
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = this.defaultBlockState();
		if (context.getLevel().getFluidState(context.getClickedPos()).is(Fluids.WATER)) {
			state = state.setValue(BlockStateProperties.WATERLOGGED, true);
		}
		ItemStack stack = context.getItemInHand();
		if (stack.getItem() instanceof CropSticksItem) {
			state = this.blockStateCropStick(state, CropStickVariant.fromItem(stack));
		} else if (stack.getItem() instanceof AgriSeedItem) {
			if (!CoreConfig.plantOffCropSticks) {
				return null;
			}
			state = this.blockStatePlant(state);
		}
		return state;
	}

	public BlockState blockStateCropStick(BlockState state, CropStickVariant variant) {
		return state.setValue(STICK_VARIANT, variant);  // crop_state should already be single_sticks
	}

	public BlockState blockStatePlant(BlockState state) {
		return state.setValue(CROP_STATE, CropState.PLANT);  // stick_variant should already be wooden
	}

	@Override
	@NotNull
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CropBlockEntity(pos, state);
	}

	private static void spawnItem(Level level, BlockPos pos, ItemStack stack) {
		level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
	}

	public static InteractionResult applyCropSticks(Level level, BlockPos pos, BlockState state, CropStickVariant variant) {
		if (variant == null) {
			return InteractionResult.FAIL;
		}
		if (level.isClientSide()) {
			return InteractionResult.PASS;
		}
		CropState cropState = state.getValue(CROP_STATE);
		BlockState newState = state;
		if (cropState == CropState.PLANT) {
			newState = state.setValue(CROP_STATE, CropState.PLANT_STICKS);
		} else if (cropState == CropState.SINGLE_STICKS && state.getValue(STICK_VARIANT) == variant) {
			newState = state.setValue(CROP_STATE, CropState.DOUBLE_STICKS);
		}
		if (newState == state) {
			return InteractionResult.FAIL;
		} else {
			level.setBlock(pos, newState, 3);
			if (cropState.hasSticks()) {
				variant.playSound(level, pos);
			} else {
				SoundType sound = Blocks.WHEAT.getSoundType(Blocks.WHEAT.defaultBlockState());
				level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
			}
			return InteractionResult.SUCCESS;
		}
	}

	public static InteractionResultHolder<CropStickVariant> removeCropSticks(Level level, BlockPos pos, BlockState state) {
		CropStickVariant stickVariant = state.getValue(STICK_VARIANT);
		if (level.isClientSide()) {
			return InteractionResultHolder.pass(stickVariant);
		}
		CropState cropState = state.getValue(CROP_STATE);
		BlockState newState = state;
		if (!cropState.hasPlant()) {
			newState = state.setValue(CROP_STATE, CropState.SINGLE_STICKS);
		}
		if (newState == state) {
			return InteractionResultHolder.fail(stickVariant);
		} else {
			level.setBlock(pos, newState, 3);
			if (cropState == CropState.DOUBLE_STICKS) {
				stickVariant.playSound(level, pos);
			}
			return InteractionResultHolder.success(stickVariant);
		}
	}

	@Override
	@NotNull
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		Optional<AgriCrop> optional = AgriApi.getCrop(level, pos);
		if (optional.isEmpty()) {
			return InteractionResult.FAIL;
		}
		AgriCrop crop = optional.get();
		// TODO: @Ketheroth future: run plant pre logic
		// run crop logic
		// do nothing from off hand
		if (hand == InteractionHand.OFF_HAND) {
			return InteractionResult.PASS;
		}
		ItemStack heldItem = player.getItemInHand(hand);
		// harvesting ord de-cross-crop'ing if empty-handed
		if (heldItem.isEmpty()) {
			if (crop.isCrossCropSticks()) {
				InteractionResultHolder<CropStickVariant> result = removeCropSticks(level, pos, state);
				if (result.getResult() == InteractionResult.SUCCESS) {
					if (!player.isCreative()) {
						spawnItem(level, pos, CropStickVariant.toItem(result.getObject()));
					}
					return InteractionResult.CONSUME;
				}
			} else if (crop.hasPlant() && crop.canBeHarvested()) {
				crop.getHarvestProducts(itemStack -> spawnItem(level, pos, itemStack));
				crop.setGrowthStage(crop.getPlant().harvestStage());
				return InteractionResult.SUCCESS;
			}
		}
		// TODO: @Ketheroth replace with item tag
		if (heldItem.is(ModItems.CLIPPER.get()) || heldItem.is(ModItems.IRON_RAKE.get()) || heldItem.is(ModItems.WOODEN_RAKE.get())) {
			return InteractionResult.PASS;
		}
		// TODO: @Ketheroth future: fertilization
		// placement of crop sticks or creation of cross crop
		if (heldItem.getItem() instanceof CropSticksItem) {
			InteractionResult result = applyCropSticks(level, pos, state, CropStickVariant.fromItem(heldItem));
			if (result == InteractionResult.SUCCESS) {
				if (!player.isCreative()) {
					player.getItemInHand(hand).shrink(1);
				}
				return result;
			}
		}
		// planting from seed
		Optional<AgriGenome> genome = AgriApi.getGenomeAdapter(heldItem).flatMap(adapter -> adapter.valueOf(heldItem));
		if (genome.isPresent()) {
			if (!crop.isCrossCropSticks() && !crop.hasPlant()) {
				player.level().setBlock(pos, state.setValue(CropBlock.CROP_STATE, CropState.PLANT_STICKS), 3);
				crop.setGenome(genome.get());
				if (!player.isCreative()) {
					player.getItemInHand(hand).shrink(1);
				}
				return InteractionResult.SUCCESS;
			} else {
				return InteractionResult.CONSUME;
			}
		}
		return InteractionResult.FAIL;
	}

	@Override
	@NotNull
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		if (level.getBlockEntity(pos) instanceof AgriCrop crop) {
			if (crop.hasPlant()) {
				// prioritize the plant if there is any
				return AgriSeedItem.toStack(crop.getGenome());
			}
			if (crop.hasCropSticks()) {
				return CropStickVariant.toItem(state.getValue(STICK_VARIANT));
			}
		}
		return super.getCloneItemStack(level, pos, state);
	}

	@Override
	protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
		if (!level.isClientSide) {
			// nothing on server
			return;
		}
		if (level.getBlockEntity(pos) instanceof AgriCrop crop) {
			// we handle the break particles ourselves to mimic the used model and spawn their particles instead of ours
			CropState cropState = state.getValue(CROP_STATE);
			if (cropState.hasSticks()) {
				ClientUtil.spawnParticlesForSticks(state.getValue(STICK_VARIANT), level, state, pos);
			}
			if (crop.hasPlant()) {
				String plantModelId = crop.getPlantId().replace(":", ":crop/") + "_stage" + crop.getGrowthStage();
				ClientUtil.spawnParticlesForPlant(plantModelId, level, state, pos);
			}
		}
	}


	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(BlockStateProperties.WATERLOGGED)) {
			level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		if (!state.canSurvive(level, pos)) {
			if (level.isClientSide() && level.getBlockEntity(pos) instanceof AgriCrop crop) {
				// we handle the break particles ourselves to mimic the used model and spawn their particles instead of ours
				String plant = crop.getPlantId().replace(":", ":crop/") + "_stage" + crop.getGrowthStage();
				ClientUtil.spawnParticlesForPlant(plant, level, state, pos);
			}
			if (state.getValue(BlockStateProperties.WATERLOGGED)) {
				return Fluids.WATER.defaultFluidState().createLegacyBlock();
			}
			return Blocks.AIR.defaultBlockState();
		}
		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return AgriApi.getSoil(level, pos.below(), level.registryAccess()).isPresent();
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
		return AgriApi.getCrop(level, pos).map(crop -> crop.isCrossCropSticks() || (crop.hasPlant() && !crop.isMaxStage())).orElse(false);
	}

	@Override
	public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
		// transfert the right click with a bonemeal to the block entity
		AgriApi.getCrop(level, pos).ifPresent(crop -> crop.onPerformBonemeal(crop, random));
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		// transfert the random tick to the block entity
		AgriApi.getCrop(level, pos).ifPresent(crop -> crop.onRandomTick(crop, random));
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
		List<ItemStack> drops = new ArrayList<>();
		BlockEntity tile = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (tile == null) {
			return drops;
		}
		CropState cropState = state.getValue(CROP_STATE);
		if (cropState.hasSticks()) {
			drops.add(CropStickVariant.toItem(state.getValue(STICK_VARIANT), cropState == CropState.DOUBLE_STICKS ? 2 : 1));
		}
		// ask the block entity for the harvest products
		if (tile instanceof AgriCrop crop) {
			crop.getHarvestProducts(drops::add);
			if (crop.hasPlant() && (crop.isMaxStage() || !CoreConfig.onlyMatureSeedDrops)) {
				drops.add(AgriSeedItem.toStack(crop.getGenome()));
			}
		}
		return drops;
	}

	@Override
	public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {
		if (state.getValue(BlockStateProperties.WATERLOGGED)) {
			level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, false), 3);
			return Fluids.WATER.getBucket().getDefaultInstance();
		}
		return ItemStack.EMPTY;
	}

	@Override
	public Optional<SoundEvent> getPickupSound() {
		// TODO: @Ketheroth wrap CropBlock in loader specific block to have the blockstate passed in
//		if (state.getValue(BlockStateProperties.WATERLOGGED)) {
//			return Fluids.WATER.getPickupSound()
//		}
		return Fluids.WATER.getPickupSound();
	}

	@Override
	public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
		return !state.getValue(BlockStateProperties.WATERLOGGED);
	}

	@Override
	public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
		if (this.canPlaceLiquid(level, pos, state, fluidState.getType()) && fluidState.getType() == Fluids.WATER) {
			if (!level.isClientSide()) {
				level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, true), 3);
				level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
			}
			return true;
		}
		return false;
	}

}
