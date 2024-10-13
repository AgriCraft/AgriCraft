package com.agricraft.agricraft.common.handler;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriSeed;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.config.CoreConfig;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.common.block.CropBlock;
import com.agricraft.agricraft.common.block.CropState;
import com.agricraft.agricraft.common.block.entity.SeedAnalyzerBlockEntity;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.agricraft.agricraft.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public class VanillaSeedConversion {

	/**
	 * @param player
	 * @param hand
	 * @param pos
	 * @param blockHitResult
	 * @return {@code true} if the right click must be canceled, false otherwise
	 */
	public static InteractionResult onRightClick(Player player, InteractionHand hand, BlockPos pos, BlockHitResult blockHitResult) {
		// TODO: @Ketheroth API: should we add item exceptions?
		ItemStack heldItem = player.getItemInHand(hand);
		if (!CoreConfig.overrideVanillaFarming
				|| heldItem.isEmpty()
				|| heldItem.getItem() instanceof AgriSeedItem
				|| AgriApi.getCrop(player.level(), pos).isPresent()) {
			return InteractionResult.PASS;
		}

		if (player.isShiftKeyDown()) {
			if (player.level().getBlockEntity(pos) instanceof SeedAnalyzerBlockEntity seedAnalyzer && !seedAnalyzer.hasSeed()) {
				// if the analyzer is empty, convert the item to an agricraft seed and let it handle the rest
				Optional<AgriGenome> genome = AgriApi.getGenomeAdapter(heldItem).flatMap(adapter -> adapter.valueOf(heldItem));
				if (genome.isPresent()) {
					seedAnalyzer.insertSeed(AgriSeedItem.toStack(genome.get()));
					heldItem.shrink(1);
					return InteractionResult.SUCCESS;
				}
			}
		}

		if (!CoreConfig.convertSeedsOnlyInAnalyzer) {
			BlockPos cropPos = pos.relative(blockHitResult.getDirection());

			return AgriApi.getGenomeAdapter(heldItem).flatMap(adapter -> adapter.valueOf(heldItem)).map(genome -> {
				// check if the seed does allow to override planting
				AgriPlant plant = AgriApi.getPlant(genome.getSpeciesGene().getTrait()).get();
				AgriSeed seed = plant.getSeed(heldItem);
				if (!seed.overridePlanting()) {
					return InteractionResult.PASS;
				}
				// the player is attempting to plant a seed, convert it to an agricraft crop
				return AgriApi.getSoil(player.level(), cropPos.below()).map(soil -> {
					// check if there are crop sticks above the soil
					InteractionResult[] consumed = new InteractionResult[]{InteractionResult.FAIL};
					boolean cropStick = AgriApi.getCrop(player.level(), cropPos).map(crop -> {
						if (!crop.hasPlant() && !crop.isCrossCropSticks()) {
							plantSeedOnCrop(player, hand, crop, genome);
							consumed[0] = InteractionResult.SUCCESS;
							return true;
						}
						return true;
					}).orElse(false);
					// if there were crop sticks, return the result of the crop sticks action
					if (cropStick) {
						return consumed[0];
					}
					// no crop sticks and is empty, try planting as a plant
					if (player.level().getBlockState(cropPos).isAir() && CoreConfig.plantOffCropSticks) {
						player.level().setBlock(cropPos, ModBlocks.CROP.get().defaultBlockState().setValue(CropBlock.CROP_STATE, CropState.PLANT), 3);
						plantSeedOnCrop(player, hand, AgriApi.getCrop(player.level(), cropPos).get(), genome);
						return InteractionResult.SUCCESS;
					}
					// nothing should happen, but return true to cancel the vanilla crop to be planted
					return InteractionResult.FAIL;
				}).orElse(InteractionResult.PASS);
			}).orElse(InteractionResult.PASS);
		}
		return InteractionResult.PASS;
	}

	private static void plantSeedOnCrop(Player player, InteractionHand hand, AgriCrop crop, AgriGenome genome) {
		crop.plantGenome(genome);
		if (player != null) {
			if (!player.isCreative()) {
				player.getItemInHand(hand).shrink(1);
			}
			player.swing(hand);
		}
	}

}
