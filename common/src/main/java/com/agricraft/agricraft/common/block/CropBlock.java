package com.agricraft.agricraft.common.block;

import com.agricraft.agricraft.client.ClientUtil;
import com.agricraft.agricraft.common.block.entity.CropBlockEntity;
import com.agricraft.agricraft.common.config.CoreConfig;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CropBlock extends Block implements EntityBlock, BonemealableBlock {

	public CropBlock() {
		super(Properties.of()
				.mapColor(MapColor.PLANT)
				.pushReaction(PushReaction.DESTROY)
				.isRedstoneConductor((state, getter, pos) -> false)
				.randomTicks()
				.noOcclusion()
				.noCollission()
				.noParticlesOnBreak()
				.lightLevel(BlockStateBase::getLightEmission)
				.sound(SoundType.CROP));
//		this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
	}

	@Override
	@NotNull
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (level.getBlockEntity(pos) instanceof CropBlockEntity cbe) {
			return cbe.getShape();
		}
		return super.getShape(state, level, pos, context);
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CropBlockEntity(pos, state);
	}

	@Override
	@NotNull
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof CropBlockEntity cbe) {
			return cbe.use(state, level, pos, player, hand, hit);
		}
		return InteractionResult.FAIL;
	}

	@Override
	@NotNull
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		if (level.getBlockEntity(pos) instanceof CropBlockEntity cbe) {
			return AgriSeedItem.toStack(cbe.getGenome());
		}
		return super.getCloneItemStack(level, pos, state);
	}

	@Override
	protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
		if (!level.isClientSide) {
			// nothing on server
			return;
		}
		if (level.getBlockEntity(pos) instanceof CropBlockEntity cbe) {
			String plant = cbe.getPlantId().replace(":", ":crop/") + "_stage" + cbe.getGrowthStage();
			ClientUtil.spawnParticlesForPlant(plant, level, state, pos);
		}
	}



	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (!state.canSurvive(level, pos)) {
			if (level.isClientSide() && level.getBlockEntity(pos) instanceof CropBlockEntity cbe) {
				String plant = cbe.getPlantId().replace(":", ":crop/") + "_stage" + cbe.getGrowthStage();
				ClientUtil.spawnParticlesForPlant(plant, level, state, pos);
			}
			return Blocks.AIR.defaultBlockState();
		}
		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return (level.getRawBrightness(pos, 0) >= 8 || level.canSeeSky(pos)) && level.getBlockState(pos.below()).is(Blocks.FARMLAND);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
		return level.getBlockEntity(pos) instanceof CropBlockEntity cbe && !cbe.isMaxStage();
	}

	@Override
	public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
		if (level.getBlockEntity(pos) instanceof CropBlockEntity cbe) {
			cbe.performBonemeal();
		}
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (level.getBlockEntity(pos) instanceof CropBlockEntity cbe) {
			cbe.randomTick(state, level, pos, random);
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
		List<ItemStack> drops = new ArrayList<>();
		BlockEntity tile = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (tile == null) {
			return drops;
		}
		// add seed and drops
		if (tile instanceof CropBlockEntity cbe) {
			cbe.getHarvestProducts(drops::add, params.getLevel().random);
			if (cbe.isMaxStage() || !CoreConfig.onlyMatureSeedDrops) {
				drops.add(AgriSeedItem.toStack(cbe.getGenome()));
			}
		}
		return drops;
	}

}
