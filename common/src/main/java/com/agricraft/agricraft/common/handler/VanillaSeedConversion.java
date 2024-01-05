package com.agricraft.agricraft.common.handler;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.config.CoreConfig;
import com.agricraft.agricraft.common.block.entity.SeedAnalyzerBlockEntity;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public class VanillaSeedConversion {

	public static void onRightClick(Player player, InteractionHand hand, BlockPos pos, BlockHitResult blockHitResult) {
		// TODO: @Ketheroth API: should we add item exceptions?
		if (!CoreConfig.overrideVanillaFarming
				|| player.getItemInHand(hand).getItem() instanceof AgriSeedItem
				|| AgriApi.getCrop(player.level(), pos).isPresent()) {
			return;
		}

		if (player.level().getBlockEntity(pos) instanceof SeedAnalyzerBlockEntity seedAnalyzer) {
			// block is seed analyzer
			// if the analyzer is empty, convert the item to an agricraft seed and let it handle the rest
			// if (player.isShiftKeyDown() && !seedAnalyzer.hasSeed()) {
			// 	AgriApi.getSeedConverter().convert(player.getItemInHand(hand)).ifPresent(seed -> player.setItemInHand(seed));
			// }
			return;
		}

		Optional<AgriSoil> optionalSoil = AgriApi.getSoil(player.level(), pos);
		if (optionalSoil.isEmpty()) {
			// there was no soil, do nothing
			return;
		}

		optionalSoil.ifPresent(soil -> {

		});

	}

}
