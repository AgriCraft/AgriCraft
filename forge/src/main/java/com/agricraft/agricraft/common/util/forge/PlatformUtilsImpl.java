package com.agricraft.agricraft.common.util.forge;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.agricraft.agricraft.common.item.forge.ForgeAgriSeedItem;
import com.agricraft.agricraft.common.registry.ModCreativeTabs;
import com.agricraft.agricraft.common.registry.ModItems;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Forge implementation of {@link com.agricraft.agricraft.common.util.PlatformUtils}
 */
public class PlatformUtilsImpl {

	public static AgriSeedItem createAgriSeedItem(Item.Properties properties) {
		return new ForgeAgriSeedItem(properties);
	}

	public static CreativeModeTab createMainCreativeTab() {
		return CreativeModeTab.builder()
				.icon(() -> new ItemStack(ModItems.DEBUGGER.get()))
				.title(Component.translatable("itemGroup.agricraft.main"))
				.displayItems(ModItems::addItemsToTabs)
//				.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
				.build();
	}

	public static CreativeModeTab createSeedsCreativeTab() {
		return CreativeModeTab.builder()
				.title(Component.translatable("itemGroup.agricraft.seeds"))
				.icon(() -> new ItemStack(Items.WHEAT_SEEDS))
				.displayItems((itemDisplayParameters, output) -> AgriApi.getPlantRegistry(ServerLifecycleHooks.getCurrentServer().registryAccess())
						.ifPresent(registry -> {
							AgriCraft.LOGGER.info("add seeds in tab: " + registry.stream().count());
							for (Map.Entry<ResourceKey<AgriPlant>, AgriPlant> entry : registry.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
								output.accept(AgriSeedItem.toStack(entry.getValue()));
							}
						}))
				.withTabsBefore(ModCreativeTabs.MAIN_TAB.getId())
				.build();
	}

	public static <T> Optional<Registry<T>> getRegistry(ResourceKey<Registry<T>> resourceKey) {
		return ServerLifecycleHooks.getCurrentServer().registryAccess().registry(resourceKey);
	}

	public static List<Item> getItemsFromTag(ResourceLocation tag) {
		return ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(tag)).stream().toList();
	}

	public static List<Block> getBlocksFromLocation(ExtraCodecs.TagOrElementLocation tag) {
		if (!tag.tag()) {
			return List.of(ForgeRegistries.BLOCKS.getValue(tag.id()));
		} else {
			return ForgeRegistries.BLOCKS.tags().getTag(BlockTags.create(tag.id())).stream().toList();
		}
	}

	public static List<Fluid> getFluidsFromLocation(ExtraCodecs.TagOrElementLocation tag) {
		if (!tag.tag()) {
			return List.of(ForgeRegistries.FLUIDS.getValue(tag.id()));
		} else {
			return ForgeRegistries.FLUIDS.tags().getTag(FluidTags.create(tag.id())).stream().toList();
		}
	}

}
