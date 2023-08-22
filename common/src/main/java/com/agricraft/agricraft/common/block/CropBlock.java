package com.agricraft.agricraft.common.block;

import com.agricraft.agricraft.common.block.entity.CropBlockEntity;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CropBlock extends Block implements EntityBlock, BonemealableBlock {

	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 7);


	public CropBlock() {
		super(Properties.of()
				.mapColor(MapColor.PLANT)
				.pushReaction(PushReaction.DESTROY)
				.isRedstoneConductor((state, getter, pos) -> false)
				.randomTicks()
				.noOcclusion()
				.noCollission()
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
			cbe.use(state, level, pos, player, hand, hit);
		}
//		if (!level.isClientSide) {
//		}
		return InteractionResult.SUCCESS;
	}

	@Override
	@NotNull
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		if (level.getBlockEntity(pos) instanceof CropBlockEntity cbe) {
			return AgriSeedItem.toStack(cbe.getPlant());
		}
		return super.getCloneItemStack(level, pos, state);
	}

	@Override
	protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
		level.levelEvent(player, 2001, pos, Block.getId(Blocks.BEETROOTS.defaultBlockState()));
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return level.getBlockState(pos.below()).is(Blocks.FARMLAND);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
		if (level.getBlockEntity(pos) instanceof CropBlockEntity cbe) {
			return cbe.getGrowthStage() < cbe.getPlant().stages().size() - 1;
		}
		return false;
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

}
