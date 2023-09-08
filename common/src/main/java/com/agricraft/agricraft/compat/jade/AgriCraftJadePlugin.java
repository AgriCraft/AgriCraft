package com.agricraft.agricraft.compat.jade;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.stat.AgriStatRegistry;
import com.agricraft.agricraft.common.block.CropBlock;
import com.agricraft.agricraft.common.block.entity.CropBlockEntity;
import com.agricraft.agricraft.common.util.LangUtils;
import com.agricraft.agricraft.common.util.PlatformUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
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

		private static final ResourceLocation ID = new ResourceLocation(AgriCraft.MOD_ID, "crop_block");

		private CropBlockComponentProvider() {
		}

		@Override
		public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
			if (blockAccessor.getBlockEntity() instanceof CropBlockEntity cbe) {
				iTooltip.add(Component.translatable("agricraft.tooltip.jade.growth", cbe.getGrowthPercent()));
				if (Minecraft.getInstance().player.isShiftKeyDown()) {
					iTooltip.add(Component.translatable("agricraft.tooltip.jade.species")
							.append(LangUtils.plantName(cbe.getGenome().getSpeciesGene().getTrait()))
					);
					AgriStatRegistry.getInstance().stream()
							.filter(stat -> !stat.isHidden())
							.map(stat -> cbe.getGenome().getStatGene(stat))
							.sorted(Comparator.comparing(p -> p.getGene().getId()))
							.map(genePair -> Component.translatable("agricraft.tooltip.jade.stat." + genePair.getGene().getId(), genePair.getTrait()))
							.forEach(iTooltip::add);
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

		private static final ResourceLocation ID = new ResourceLocation(AgriCraft.MOD_ID, "soil");

		private SoilComponentProvider() {
		}

		@Override
		public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
			Optional<AgriSoil> soil = PlatformUtils.getSoilFromBlock(blockAccessor.getBlockState());
			if (soil.isPresent() && Minecraft.getInstance().player.isShiftKeyDown()) {
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
