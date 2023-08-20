package com.agricraft.agricraft.common.util.fabric;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.agricraft.agricraft.common.registry.ModItems;
import com.agricraft.agricraft.common.util.PlatformUtils;
import com.agricraft.agricraft.fabric.AgriCraftFabric;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Map;

public class PlatformUtilsImpl {


	public static AgriSeedItem createAgriSeedItem(Item.Properties properties) {
		return new AgriSeedItem( properties);
	}


	public static CreativeModeTab createMainCreativeTab() {
		return FabricItemGroup.builder()
				.icon(() -> new ItemStack(ModItems.DEBUGGER.get()))
				.title(Component.translatable("itemGroup.agricraft.main"))
				.displayItems(ModItems::addItemsToTabs)
				.build();
	}

	public static CreativeModeTab createSeedsCreativeTab() {
		return FabricItemGroup.builder()
				.title(Component.translatable("itemGroup.agricraft.seeds"))
				.icon(() ->new ItemStack(Items.WHEAT_SEEDS))
				.displayItems((itemDisplayParameters, output) -> {
					if (AgriCraftFabric.cachedServer != null) {
						Registry<AgriPlant> registry = AgriCraftFabric.cachedServer.registryAccess().registry(PlatformUtils.getPlantRegistryKey()).get();
						AgriCraft.LOGGER.info("add seeds in tab: " + registry.stream().count());
						for (Map.Entry<ResourceKey<AgriPlant>, AgriPlant> entry : registry.entrySet()) {
							output.accept(AgriSeedItem.toStack(entry.getValue()));
						}
					} else {
						AgriCraft.LOGGER.info("cached server is null");
					}
				})
				.build();
	}

	public static String getIdFromPlant(AgriPlant plant) {
		if (AgriCraftFabric.cachedServer == null) {
			return "";
		}
		return AgriCraftFabric.cachedServer.registryAccess().registry(PlatformUtils.getPlantRegistryKey()).get().getKey(plant).toString();
	}

	public static AgriPlant getPlantFromId(String id) {
		if (AgriCraftFabric.cachedServer == null) {
			return null;
		}
		return AgriCraftFabric.cachedServer.registryAccess().registry(PlatformUtils.getPlantRegistryKey()).get().get(new ResourceLocation(id));
	}
}
