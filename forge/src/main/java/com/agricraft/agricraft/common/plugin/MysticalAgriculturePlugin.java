package com.agricraft.agricraft.common.plugin;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.agricraft.agricraft.common.registry.ModBlocks;
import com.agricraft.agricraft.common.registry.ModItems;
import com.blakebr0.mysticalagriculture.api.MysticalAgricultureAPI;
import com.blakebr0.mysticalagriculture.api.crop.Crop;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = AgriApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MysticalAgriculturePlugin {

	@SubscribeEvent
	public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
		event.register((stack, tintIndex) -> {
			String species = AgriSeedItem.getSpecies(stack);
			if (species != null && !species.equals("agricraft:unknown")) {
				Crop crop = MysticalAgricultureAPI.getCropRegistry().getCropById(new ResourceLocation(species));
				if (crop != null && crop.isSeedColored()) {
					return crop.getSeedColor();
				}
			}
			return -1;
		}, ModItems.SEED.get());
	}

	@SubscribeEvent
	public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
		event.register((state, level, pos, tintIndex) -> {
			Optional<AgriCrop> optional = AgriApi.getCrop(level, pos);
			if (optional.isPresent() && optional.get().hasPlant()) {
				String species = optional.get().getGenome().getSpeciesGene().getTrait();
				Crop crop = MysticalAgricultureAPI.getCropRegistry().getCropById(new ResourceLocation(species));
				if (crop != null && crop.isFlowerColored()) {
					return crop.getFlowerColor();
				}
			}
			return -1;
		}, ModBlocks.CROP.get());
	}

}
