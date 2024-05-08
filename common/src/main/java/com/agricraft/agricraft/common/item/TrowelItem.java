package com.agricraft.agricraft.common.item;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.config.CoreConfig;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.crop.AgriGrowthStage;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.api.genetic.AgriGenomeProviderItem;
import com.agricraft.agricraft.common.block.CropBlock;
import com.agricraft.agricraft.common.registry.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TrowelItem extends Item implements AgriGenomeProviderItem {

	public TrowelItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		return AgriApi.getCrop(context.getLevel(), context.getClickedPos())
				.map(crop -> tryUseOnCrop(crop, context.getItemInHand(), context.getPlayer()))
				.orElseGet(() -> tryPlantOnSoil(context.getLevel(), context.getClickedPos(), context.getItemInHand(), context.getPlayer()));
	}

	protected InteractionResult tryUseOnCrop(AgriCrop crop, ItemStack heldItem, Player player) {
		if (crop.hasWeeds()) {
			if (player != null && player.level().isClientSide()) {
				player.sendSystemMessage(Component.translatable("agricraft.message.trowel_weed"));
			}
			return InteractionResult.FAIL;
		} else if (crop.isCrossCropSticks()) {
			return InteractionResult.FAIL;
		}
		if (crop.hasPlant()) {
			// Try picking up a plant onto the trowel
			return tryPickUpPlant(crop, heldItem, player);
		}
		// Try planting a plant on crop sticks
		return tryPlantOnCropSticks(crop, heldItem, player);
	}

	protected InteractionResult tryPickUpPlant(AgriCrop crop, ItemStack stack, Player player) {
		if (crop.getLevel() == null || crop.getLevel().isClientSide()) {
			return InteractionResult.PASS;
		}
		if (this.hasPlant(stack)) {
			if (player != null) {
				player.sendSystemMessage(Component.translatable("agricraft.message.trowel_plant"));
			}
			return InteractionResult.FAIL;
		} else {
			AgriGenome genome = crop.getGenome();
			if (genome != null) {
				this.setPlant(stack, genome, crop.getGrowthStage());
				crop.removeGenome();
			}
			return InteractionResult.SUCCESS;
		}
	}

	protected InteractionResult tryPlantOnCropSticks(AgriCrop crop, ItemStack stack, Player player) {
		if (crop.getLevel() == null || crop.getLevel().isClientSide()) {
			return InteractionResult.PASS;
		}
		if (crop.isCrossCropSticks()) {
			return InteractionResult.FAIL;
		}
		if (this.hasPlant(stack)) {
			if (crop.hasCropSticks()) {
				this.getGenome(stack).ifPresent(genome ->
						this.getGrowthStage(stack).ifPresent(growth -> {
							this.removePlant(stack);
							crop.plantGenome(genome, player);
							crop.setGrowthStage(growth);
						}));
			}
			return InteractionResult.SUCCESS;
		} else {
			if (player != null) {
				player.sendSystemMessage(Component.translatable("agricraft.message.trowel_no_plant"));
			}
			return InteractionResult.FAIL;
		}
	}

	protected InteractionResult tryPlantOnSoil(Level level, BlockPos pos, ItemStack stack, Player player) {
		if (this.hasPlant(stack)) {
			return AgriApi.getCrop(level, pos.above())
					.map(crop -> this.tryPlantOnCropSticks(crop, stack, player))
					.orElseGet(() -> this.tryNewPlant(level, pos.above(), stack, player));
		}
		return InteractionResult.FAIL;
	}

	protected InteractionResult tryNewPlant(Level world, BlockPos pos, ItemStack stack, @Nullable Player player) {
		if (CoreConfig.plantOffCropSticks) {
			CropBlock cropBlock = (CropBlock) ModBlocks.CROP.get();
			BlockState newState = cropBlock.blockStatePlant(cropBlock.defaultBlockState());
			if (world.setBlock(pos, newState, 3)) {
				boolean success = AgriApi.getCrop(world, pos).map(crop -> this.getGenome(stack).map(genome ->
								this.getGrowthStage(stack).map(stage -> {
									crop.plantGenome(genome, player);
									crop.setGrowthStage(stage);
									this.removePlant(stack);
									return true;
								}).orElse(false))
						.orElse(false)).orElse(false);
				if (success) {
					return InteractionResult.SUCCESS;
				} else {
					world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
				}
			}
		}
		return InteractionResult.FAIL;
	}

	public void setPlant(ItemStack stack, AgriGenome genome, AgriGrowthStage stage) {
		if (this.hasPlant(stack)) {
			return;
		}
		this.setGenome(stack, genome);
		CompoundTag tag = stack.getOrCreateTag();
		tag.putInt("growthIndex", stage.index());
		tag.putInt("growthTotal", stage.total());
	}

	public void removePlant(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		if (tag != null) {
			tag.remove("growthIndex");
			tag.remove("growthTotal");
			AgriGenome.removeFromNBT(tag);
		}
	}

	public boolean hasPlant(ItemStack itemStack) {
		return this.getGenome(itemStack).isPresent();
	}

	public Optional<AgriGrowthStage> getGrowthStage(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains("growthIndex") && tag.contains("growthTotal")) {
			int growthIndex = tag.getInt("growthIndex");
			int growthTotal = tag.getInt("growthTotal");
			return Optional.of(new AgriGrowthStage(growthIndex, growthTotal));
		}
		return Optional.empty();
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
		tooltipComponents.add(Component.translatable("agricraft.tooltip.trowel").withStyle(ChatFormatting.DARK_GRAY));
		CompoundTag tag = stack.getTag();
		if (tag != null) {
			AgriGenome genome = AgriGenome.fromNBT(tag);
			if (genome != null) {
				genome.appendHoverText(tooltipComponents, TooltipFlag.ADVANCED);
			}
		}
	}

}

