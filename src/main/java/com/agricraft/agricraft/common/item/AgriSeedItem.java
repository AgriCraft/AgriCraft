package com.agricraft.agricraft.common.item;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.LangUtils;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.common.block.CropBlock;
import com.agricraft.agricraft.common.block.CropState;
import com.agricraft.agricraft.common.block.entity.SeedAnalyzerBlockEntity;
import com.agricraft.agricraft.common.registry.AgriBlocks;
import com.agricraft.agricraft.common.registry.AgriDataComponents;
import com.agricraft.agricraft.common.registry.AgriItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class AgriSeedItem extends BlockItem {

	public AgriSeedItem(Properties properties) {
		super(AgriBlocks.CROP.get(), properties);
	}

	/**
	 * Create an ItemStack with the default genome for a plant
	 *
	 * @param plant the plant to create to genome from
	 * @return an ItemStack with the default genome of the plant
	 */
	public static ItemStack toStack(AgriPlant plant) {
		ItemStack stack = new ItemStack(AgriItems.SEED.get());
		stack.set(AgriDataComponents.GENOME, new AgriGenome(plant));
		return stack;
	}

	/**
	 * Create an ItemStack with the given genome
	 *
	 * @param genome the genome to create the ItemStack from
	 * @return an ItemStack with the given genome
	 */
	public static ItemStack toStack(AgriGenome genome) {
		ItemStack stack = new ItemStack(AgriItems.SEED.get());
		stack.set(AgriDataComponents.GENOME, genome);
		return stack;
	}

	/**
	 * Compute the plant species from an ItemStack
	 *
	 * @param stack the itemstack to compute the species from
	 * @return the plant species formatted as a resource location, or {@code agricraft:unknown} if not found
	 */
	public static String getSpecies(ItemStack stack) {
		if (stack.getItem() != AgriItems.SEED.get()) {
			return "agricraft:unknown";
		}
		AgriGenome genome = stack.get(AgriDataComponents.GENOME);
		if (genome == null) {
			return "agricraft:unknown";
		}
		return genome.species().trait();
	}

	@Override
	public Component getName(ItemStack stack) {
		AgriGenome genome = stack.get(AgriDataComponents.GENOME);
		if (genome == null) {
			return Component.translatable("seed.agricraft.agricraft.unknown");
		}
		return LangUtils.seedName(genome.species().trait());
	}

	@Override
	public InteractionResult place(BlockPlaceContext context) {
		InteractionResult result = super.place(context);
		Level level = context.getLevel();
		if (result.consumesAction()) {
			BlockPos pos = context.getClickedPos();
			AgriApi.get().getCrop(level, pos).ifPresent(crop -> {
				AgriGenome genome = context.getItemInHand().get(AgriDataComponents.GENOME);
				if (genome != null) {
					crop.plantGenome(genome);
					level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(CropBlock.CROP_STATE, CropState.PLANT));
				}
			});
		}
		return result;
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		ItemStack heldItem = context.getItemInHand();
		// if crop sticks were clicked, attempt to plant the seed
		Optional<AgriCrop> optionalAgriCrop = AgriApi.get().getCrop(level, context.getClickedPos());
		if (optionalAgriCrop.isPresent()) {
			AgriCrop crop = optionalAgriCrop.get();
			if (crop.hasPlant() || crop.isCrossCropSticks()) {
				return InteractionResult.PASS;
			}
			plantSeed(context.getPlayer(), crop, heldItem);
			return InteractionResult.CONSUME;
		}
		// if a seed analyzer was clicked, insert the seed inside
		if (level.getBlockEntity(context.getClickedPos()) instanceof SeedAnalyzerBlockEntity seedAnalyzer) {
			return seedAnalyzer.insertSeed(heldItem, context.getPlayer()) ? InteractionResult.SUCCESS : InteractionResult.PASS;
		}
		// if a soil was clicked, check the block above and handle accordingly
		return AgriApi.get().getSoil(level, context.getClickedPos()).map(soil -> AgriApi.get().getCrop(level, context.getClickedPos().above()).map(crop -> {
			// there is a crop with a plant or is a cross crop stick, do nothing
			if (crop.hasPlant() || crop.isCrossCropSticks()) {
				return InteractionResult.PASS;
			}
			// there is a crop without a plant, plant the seed in the crop
			plantSeed(context.getPlayer(), crop, heldItem);
			return InteractionResult.CONSUME;
		}).orElse(InteractionResult.PASS)).orElse(super.useOn(context));
	}

	private void plantSeed(Player player, AgriCrop crop, ItemStack seed) {
		AgriGenome genome = seed.get(AgriDataComponents.GENOME);
		if (genome != null) {
			crop.plantGenome(genome, player);
			if (player != null && !player.isCreative()) {
				seed.shrink(1);
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		AgriGenome genome = stack.get(AgriDataComponents.GENOME);
		if (genome != null) {
			genome.appendHoverText(tooltipComponents, tooltipFlag);
		}
	}

//	@Override
//	public ItemStack getDefaultInstance() {
//		return AgriApi.getPlant(new ResourceLocation("minecraft:wheat")).map(AgriSeedItem::toStack).orElse(super.getDefaultInstance());
//	}

}
