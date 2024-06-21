package com.agricraft.agricraft.compat.jade;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.requirement.AgriGrowthResponse;
import com.agricraft.agricraft.api.stat.AgriStatRegistry;
import com.agricraft.agricraft.common.block.CropBlock;
import com.agricraft.agricraft.common.util.LangUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

import java.util.Comparator;
import java.util.Optional;

@WailaPlugin
public class AgriCraftJadePlugin implements IWailaPlugin {

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerBlockComponent(CropBlockComponentProvider.INSTANCE, CropBlock.class);
		registration.registerBlockComponent(SoilComponentProvider.INSTANCE, Block.class);
	}

	public static class CropBlockComponentProvider implements IBlockComponentProvider {

		public static final CropBlockComponentProvider INSTANCE = new CropBlockComponentProvider();

		private static final ResourceLocation ID = new ResourceLocation(AgriApi.MOD_ID, "crop_block");

		private CropBlockComponentProvider() {
		}

		@Override
		public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
			if (blockAccessor.getBlockEntity() instanceof AgriCrop crop) {
				if (crop.hasPlant()) {
					iTooltip.add(Component.translatable("agricraft.tooltip.jade.growth", crop.getGrowthPercent() * 100));
					if (blockAccessor.getPlayer().isShiftKeyDown()) {
						iTooltip.add(Component.translatable("agricraft.tooltip.jade.species")
								.append(LangUtils.plantName(crop.getGenome().getSpeciesGene().getTrait()))
						);
						AgriStatRegistry.getInstance().stream()
								.filter(stat -> !stat.isHidden())
								.map(stat -> crop.getGenome().getStatGene(stat))
								.sorted(Comparator.comparing(p -> p.getGene().getId()))
								.map(genePair -> Component.translatable("agricraft.tooltip.jade.stat." + genePair.getGene().getId(), genePair.getTrait()))
								.forEach(iTooltip::add);
						AgriGrowthResponse response = crop.getFertilityResponse();
						iTooltip.add(Component.translatable("agricraft.tooltip.magnifying.requirement." + (response.isLethal() ? "lethal" : response.isFertile() ? "fertile" : "not_fertile")));
					}
				} else {
					iTooltip.add(Component.translatable("agricraft.tooltip.magnifying.no_plant"));
				}
				if (crop.hasWeeds()) {
					iTooltip.add(Component.translatable("agricraft.tooltip.magnifying.weeds").append(LangUtils.weedName(crop.getWeedId())));
					if (blockAccessor.getPlayer().isShiftKeyDown()) {
						iTooltip.add(Component.literal("  ").append(Component.translatable("agricraft.tooltip.magnifying.growth", crop.getWeedGrowthStage().index() + 1, crop.getWeedGrowthStage().total())));
					}
				}
			}
		}

		@Override
		public ResourceLocation getUid() {
			return ID;
		}

	}

	public static class SoilComponentProvider implements IBlockComponentProvider {

		public static final SoilComponentProvider INSTANCE = new SoilComponentProvider();

		private static final ResourceLocation ID = new ResourceLocation(AgriApi.MOD_ID, "soil");

		private SoilComponentProvider() {
		}

		@Override
		public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
			Optional<AgriSoil> soil = AgriApi.getSoil(blockAccessor.getLevel(), blockAccessor.getPosition(), blockAccessor.getLevel().registryAccess());
			if (soil.isPresent() && blockAccessor.getPlayer().isShiftKeyDown()) {
				AgriSoil soil1 = soil.get();
				iTooltip.add(Component.translatable("agricraft.tooltip.magnifying.soil.humidity")
						.append(Component.translatable("agricraft.soil.humidity." + soil1.humidity().name().toLowerCase())));
				iTooltip.add(Component.translatable("agricraft.tooltip.magnifying.soil.acidity")
						.append(Component.translatable("agricraft.soil.acidity." + soil1.acidity().name().toLowerCase())));
				iTooltip.add(Component.translatable("agricraft.tooltip.magnifying.soil.nutrients")
						.append(Component.translatable("agricraft.soil.nutrients." + soil1.nutrients().name().toLowerCase())));
			}
		}

		@Override
		public ResourceLocation getUid() {
			return ID;
		}

	}

}
