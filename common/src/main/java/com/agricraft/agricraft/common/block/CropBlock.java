package com.agricraft.agricraft.common.block;

import com.agricraft.agricraft.common.block.entity.CropBlockEntity;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CropBlock extends Block implements EntityBlock {

	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 7);;

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
		this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CropBlockEntity(pos, state);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!level.isClientSide) {
			if (level.getBlockEntity(pos) instanceof CropBlockEntity cbe) {
				cbe.use(state, level, pos, player, hand, hit);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
		if (level.getBlockEntity(pos) instanceof CropBlockEntity cbe) {
			return AgriSeedItem.toStack(new ResourceLocation(cbe.getSeed()));
		}
		return super.getCloneItemStack(level, pos, state);
	}

	@Override
	protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
		super.spawnDestroyParticles(level, player, pos, state);
	}

}
