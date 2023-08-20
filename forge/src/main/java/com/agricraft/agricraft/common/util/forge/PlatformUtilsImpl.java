package com.agricraft.agricraft.common.util.forge;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.agricraft.agricraft.common.item.forge.ForgeAgriSeedItem;
import com.agricraft.agricraft.common.registry.ModCreativeTabs;
import com.agricraft.agricraft.common.registry.ModItems;
import com.agricraft.agricraft.common.util.PlatformUtils;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Map;

public class PlatformUtilsImpl {

	public static AgriSeedItem createAgriSeedItem(Item.Properties properties) {
		return new ForgeAgriSeedItem(properties);
	}

	public static CreativeModeTab createMainCreativeTab() {
		return CreativeModeTab.builder()
				.icon(() -> new ItemStack(ModItems.DEBUGGER.get()))
				.title(Component.translatable("itemGroup.agricraft.main"))
				.displayItems(ModItems::addItemsToTabs)
				.withTabsBefore(CreativeModeTabs.FUNCTIONAL_BLOCKS)
				.build();
	}

	public static CreativeModeTab createSeedsCreativeTab() {
		return CreativeModeTab.builder()
				.title(Component.translatable("itemGroup.agricraft.seeds"))
				.icon(() -> new ItemStack(Items.WHEAT_SEEDS))
				.displayItems((itemDisplayParameters, output) -> {
					Registry<AgriPlant> registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registry(PlatformUtils.getPlantRegistryKey()).get();
					AgriCraft.LOGGER.info("add seeds in tab: " + registry.stream().count());
					for (Map.Entry<ResourceKey<AgriPlant>, AgriPlant> entry : registry.entrySet()) {
						output.accept(AgriSeedItem.toStack(entry.getValue()));
					}
				})
				.withTabsBefore(ModCreativeTabs.MAIN_TAB.getId())
				.build();
	}

	public static String getIdFromPlant(AgriPlant plant) {
		return ServerLifecycleHooks.getCurrentServer().registryAccess().registry(PlatformUtils.getPlantRegistryKey()).get().getKey(plant).toString();
	}

	public static AgriPlant getPlantFromId(String id) {
		return ServerLifecycleHooks.getCurrentServer().registryAccess().registry(PlatformUtils.getPlantRegistryKey()).get().get(new ResourceLocation(id));
	}
}
