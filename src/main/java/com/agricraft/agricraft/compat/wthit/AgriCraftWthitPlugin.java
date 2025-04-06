package com.agricraft.agricraft.compat.wthit;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.LangUtils;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.requirement.AgriGrowthResponse;
import com.agricraft.agricraft.common.block.CropBlock;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.Comparator;
import java.util.Optional;

public class AgriCraftWthitPlugin implements IWailaPlugin {

	@Override
	public void register(IRegistrar registrar) {
		registrar.addComponent(CropBlockComponentProvider.INSTANCE, TooltipPosition.BODY, CropBlock.class);
		registrar.addComponent(SoilComponentProvider.INSTANCE, TooltipPosition.BODY, Block.class);
	}

	public static class CropBlockComponentProvider implements IBlockComponentProvider {

		public static final CropBlockComponentProvider INSTANCE = new CropBlockComponentProvider();

		@Override
		public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
			if (accessor.getBlockEntity() instanceof AgriCrop crop) {
				if (crop.hasPlant()) {
					tooltip.addLine(Component.translatable("agricraft.tooltip.jade.growth", crop.getGrowthPercent() * 100));
					if (accessor.getPlayer().isShiftKeyDown()) {
						tooltip.addLine(Component.translatable("agricraft.tooltip.jade.species")
								.append(LangUtils.plantName(crop.getGenome().species().trait()))
						);
						AgriApi.get().getStatRegistry().stream()
								.filter(stat -> !stat.isHidden())
								.map(stat -> crop.getGenome().getStatChromosome(stat))
								.sorted(Comparator.comparing(c -> c.gene().getId()))
								.map(chromosome -> Component.translatable("agricraft.tooltip.jade.stat." + chromosome.gene().getId(), chromosome.trait()))
								.forEach(tooltip::addLine);
						AgriGrowthResponse response = crop.getFertilityResponse();
						tooltip.addLine(Component.translatable("agricraft.tooltip.magnifying.requirement." + (response.isLethal() ? "lethal" : response.isFertile() ? "fertile" : "not_fertile")));
					}
				} else {
					tooltip.addLine(Component.translatable("agricraft.tooltip.magnifying.no_plant"));
				}
				if (crop.hasWeeds()) {
					tooltip.addLine(Component.translatable("agricraft.tooltip.magnifying.weeds").append(LangUtils.weedName(crop.getWeedId().toString())));
					if (accessor.getPlayer().isShiftKeyDown()) {
						tooltip.addLine(Component.literal("  ").append(Component.translatable("agricraft.tooltip.magnifying.growth", crop.getWeedGrowthStage().index() + 1, crop.getWeedGrowthStage().total())));
					}
				}
			}
		}

	}

	public static class SoilComponentProvider implements IBlockComponentProvider {

		public static final SoilComponentProvider INSTANCE = new SoilComponentProvider();

		@Override
		public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
			Optional<AgriSoil> soil = AgriApi.get().getSoil(accessor.getWorld(), accessor.getPosition(), accessor.getWorld().registryAccess());
			if (soil.isPresent() && accessor.getPlayer().isShiftKeyDown()) {
				AgriSoil soil1 = soil.get();
				tooltip.addLine(Component.translatable("agricraft.tooltip.magnifying.soil.humidity")
						.append(Component.translatable("agricraft.soil.humidity." + soil1.humidity().name().toLowerCase())));
				tooltip.addLine(Component.translatable("agricraft.tooltip.magnifying.soil.acidity")
						.append(Component.translatable("agricraft.soil.acidity." + soil1.acidity().name().toLowerCase())));
				tooltip.addLine(Component.translatable("agricraft.tooltip.magnifying.soil.nutrients")
						.append(Component.translatable("agricraft.soil.nutrients." + soil1.nutrients().name().toLowerCase())));
			}
		}

	}
}
