package com.agricraft.agricraft.common.util.forge;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.common.codecs.AgriSeed;
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
import net.minecraft.world.level.block.Block;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Map;

public class PlatformUtilsImpl {

	public static final ResourceKey<Registry<AgriSeed>> AGRISEEDS = ResourceKey.createRegistryKey(new ResourceLocation(AgriCraft.MOD_ID, "seed"));

	public static AgriSeedItem createAgriSeedItem(Block block, Item.Properties properties) {
		return new ForgeAgriSeedItem(block, properties);
	}

	public static ResourceKey<Registry<AgriSeed>> getSeedRegistryKey() {
		return AGRISEEDS;
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
					Registry<AgriSeed> registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registry(PlatformUtils.getSeedRegistryKey()).get();
					for (Map.Entry<ResourceKey<AgriSeed>, AgriSeed> entry : registry.entrySet()) {
						output.accept(AgriSeedItem.toStack(entry.getKey().location()));
					}
				})
				.withTabsBefore(ModCreativeTabs.MAIN_TAB.getId())
				.build();
	}

}
