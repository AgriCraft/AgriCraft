package com.agricraft.agricraft.common.util.fabric;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.common.codecs.AgriSeed;
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

	public static final ResourceKey<Registry<AgriSeed>> AGRISEEDS = ResourceKey.createRegistryKey(new ResourceLocation(AgriCraft.MOD_ID, "seed"));

	public static AgriSeedItem createAgriSeedItem(Item.Properties properties) {
		return new AgriSeedItem(properties);
	}

	public static ResourceKey<Registry<AgriSeed>> getSeedRegistryKey() {
		return AGRISEEDS;
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
						Registry<AgriSeed> registry = AgriCraftFabric.cachedServer.registryAccess().registry(PlatformUtils.getSeedRegistryKey()).get();
						AgriCraft.LOGGER.info("add seeds in tab: " + registry.stream().count());
						for (Map.Entry<ResourceKey<AgriSeed>, AgriSeed> entry : registry.entrySet()) {
							output.accept(AgriSeedItem.toStack(entry.getKey().location()));
						}
					} else {
						AgriCraft.LOGGER.info("cached server is null");
					}
				})
				.build();
	}

}
