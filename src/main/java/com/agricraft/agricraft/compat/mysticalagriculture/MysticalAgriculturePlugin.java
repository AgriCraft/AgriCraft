package com.agricraft.agricraft.compat.mysticalagriculture;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.agricraft.agricraft.common.registry.AgriBlocks;
import com.agricraft.agricraft.common.registry.AgriItems;
import com.blakebr0.mysticalagriculture.api.MysticalAgricultureAPI;
import com.blakebr0.mysticalagriculture.api.crop.Crop;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.Optional;

@EventBusSubscriber(modid = AgriApi.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MysticalAgriculturePlugin {

	@SubscribeEvent
	public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
		if (ModList.get().isLoaded("mysticalagriculture")) {
			event.register((stack, tintIndex) -> {
				String species = AgriSeedItem.getSpecies(stack);
				if (species != null && !species.equals("agricraft:unknown")) {
					Crop crop = MysticalAgricultureAPI.getCropRegistry().getCropById(ResourceLocation.parse(species));
					if (crop != null && crop.isSeedColored()) {
						return crop.getSeedColor();
					}
				}
				return -1;
			}, AgriItems.SEED.get());
		}
	}

	@SubscribeEvent
	public static void registerItemColors(RegisterColorHandlersEvent.Block event) {
		if (ModList.get().isLoaded("mysticalagriculture")) {
			event.register((state, level, pos, tintIndex) -> {
				Optional<AgriCrop> optional = AgriApi.get().getCrop(level, pos);
				if (optional.isPresent() && optional.get().hasPlant()) {
					String species = optional.get().getGenome().species().trait();
					Crop crop = MysticalAgricultureAPI.getCropRegistry().getCropById(ResourceLocation.parse(species));
					if (crop != null && crop.isFlowerColored()) {
						return crop.getFlowerColor();
					}
				}
				return -1;
			}, AgriBlocks.CROP.get());
		}
	}

}
