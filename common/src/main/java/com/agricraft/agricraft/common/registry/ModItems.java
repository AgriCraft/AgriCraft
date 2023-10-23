package com.agricraft.agricraft.common.registry;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.common.block.CropStickVariant;
import com.agricraft.agricraft.common.item.ClipperItem;
import com.agricraft.agricraft.common.item.CropSticksItem;
import com.agricraft.agricraft.common.item.DebuggerItem;
import com.agricraft.agricraft.common.item.JournalItem;
import com.agricraft.agricraft.common.item.MagnifyingGlassItem;
import com.agricraft.agricraft.common.item.RakeItem;
import com.agricraft.agricraft.common.util.PlatformUtils;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class ModItems {
	public static final ResourcefulRegistry<Item> ITEMS = ResourcefulRegistries.create(BuiltInRegistries.ITEM, AgriApi.MOD_ID);

	public static final RegistryEntry<Item> DEBUGGER = ITEMS.register("debugger", () -> new DebuggerItem(new Item.Properties()));

	public static final RegistryEntry<Item> JOURNAL = ITEMS.register("journal", () -> new JournalItem(new Item.Properties()));
	public static final RegistryEntry<Item> CLIPPER = ITEMS.register("clipper", () -> new ClipperItem(new Item.Properties().stacksTo(1)));
	public static final RegistryEntry<Item> MAGNIFYING_GLASS = ITEMS.register("magnifying_glass", () -> new MagnifyingGlassItem(new Item.Properties().stacksTo(1)));
	public static final RegistryEntry<Item> WOODEN_RAKE = ITEMS.register("wooden_rake", () -> new RakeItem(new Item.Properties()));
	public static final RegistryEntry<Item> IRON_RAKE = ITEMS.register("iron_rake", () -> new RakeItem(new Item.Properties()));
	public static final RegistryEntry<Item> WOODEN_CROP_STICKS = ITEMS.register("wooden_crop_sticks", () -> new CropSticksItem(ModBlocks.CROP.get(), CropStickVariant.WOODEN));
	public static final RegistryEntry<Item> IRON_CROP_STICKS = ITEMS.register("iron_crop_sticks", () -> new CropSticksItem(ModBlocks.CROP.get(), CropStickVariant.IRON));
	public static final RegistryEntry<Item> OBSIDIAN_CROP_STICKS = ITEMS.register("obsidian_crop_sticks", () -> new CropSticksItem(ModBlocks.CROP.get(), CropStickVariant.OBSIDIAN));
	public static final RegistryEntry<Item> SEED = ITEMS.register("seed", () -> PlatformUtils.createAgriSeedItem(new Item.Properties()));


	public static void addItemsToTabs(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
		if (itemDisplayParameters.hasPermissions()) {
			output.accept(ModItems.DEBUGGER.get());
		}
		output.accept(ModItems.JOURNAL.get());
		output.accept(ModItems.CLIPPER.get());
		output.accept(ModItems.MAGNIFYING_GLASS.get());
		output.accept(ModItems.WOODEN_RAKE.get());
		output.accept(ModItems.IRON_RAKE.get());
		output.accept(ModItems.WOODEN_CROP_STICKS.get());
		output.accept(ModItems.IRON_CROP_STICKS.get());
		output.accept(ModItems.OBSIDIAN_CROP_STICKS.get());
	}

}
