package com.agricraft.agricraft.common.registry;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.common.block.CropStickVariant;
import com.agricraft.agricraft.common.item.ClipperItem;
import com.agricraft.agricraft.common.item.CropSticksItem;
import com.agricraft.agricraft.common.item.DebuggerItem;
import com.agricraft.agricraft.common.item.JournalItem;
import com.agricraft.agricraft.common.item.MagnifyingGlassItem;
import com.agricraft.agricraft.common.item.RakeItem;
import com.agricraft.agricraft.common.item.SeedBagItem;
import com.agricraft.agricraft.common.item.TrowelItem;
import com.agricraft.agricraft.common.util.Platform;
import com.agricraft.agricraft.common.util.PlatformRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class ModItems {

	public static final PlatformRegistry<Item> ITEMS = Platform.get().createRegistry(BuiltInRegistries.ITEM, AgriApi.MOD_ID);

	public static final PlatformRegistry.Entry<Item> DEBUGGER = ITEMS.register("debugger", () -> new DebuggerItem(new Item.Properties()));

	public static final PlatformRegistry.Entry<Item> JOURNAL = ITEMS.register("journal", () -> new JournalItem(new Item.Properties()));
	public static final PlatformRegistry.Entry<Item> SEED_ANALYZER = ITEMS.register("seed_analyzer", () -> new BlockItem(ModBlocks.SEED_ANALYZER.get(), new Item.Properties()));
	public static final PlatformRegistry.Entry<Item> CLIPPER = ITEMS.register("clipper", () -> new ClipperItem(new Item.Properties().stacksTo(1)));
	public static final PlatformRegistry.Entry<Item> MAGNIFYING_GLASS = ITEMS.register("magnifying_glass", () -> new MagnifyingGlassItem(new Item.Properties().stacksTo(1)));
	public static final PlatformRegistry.Entry<Item> WOODEN_RAKE = ITEMS.register("wooden_rake", () -> new RakeItem(new Item.Properties(), RakeItem.WOOD_LOGIC));
	public static final PlatformRegistry.Entry<Item> IRON_RAKE = ITEMS.register("iron_rake", () -> new RakeItem(new Item.Properties(), RakeItem.IRON_LOGIC));
	public static final PlatformRegistry.Entry<Item> TROWEL = ITEMS.register("trowel", () -> new TrowelItem(new Item.Properties().stacksTo(1)));
	public static final PlatformRegistry.Entry<Item> WOODEN_CROP_STICKS = ITEMS.register("wooden_crop_sticks", () -> new CropSticksItem(ModBlocks.CROP.get(), CropStickVariant.WOODEN));
	public static final PlatformRegistry.Entry<Item> IRON_CROP_STICKS = ITEMS.register("iron_crop_sticks", () -> new CropSticksItem(ModBlocks.CROP.get(), CropStickVariant.IRON));
	public static final PlatformRegistry.Entry<Item> OBSIDIAN_CROP_STICKS = ITEMS.register("obsidian_crop_sticks", () -> new CropSticksItem(ModBlocks.CROP.get(), CropStickVariant.OBSIDIAN));
	public static final PlatformRegistry.Entry<Item> SEED_BAG = ITEMS.register("seed_bag", () -> new SeedBagItem(new Item.Properties().stacksTo(1)));
	public static final PlatformRegistry.Entry<Item> SEED = ITEMS.register("seed", () -> Platform.get().createAgriSeedItem(new Item.Properties()));

	public static final PlatformRegistry.Entry<Item> COAL_PEBBLE = ITEMS.register("coal_pebble", () -> new Item(new Item.Properties()));
	public static final PlatformRegistry.Entry<Item> COPPER_NUGGET = ITEMS.register("copper_nugget", () -> new Item(new Item.Properties()));
	public static final PlatformRegistry.Entry<Item> DIAMOND_SHARD = ITEMS.register("diamond_shard", () -> new Item(new Item.Properties()));
	public static final PlatformRegistry.Entry<Item> EMERALD_SHARD = ITEMS.register("emerald_shard", () -> new Item(new Item.Properties()));
	public static final PlatformRegistry.Entry<Item> QUARTZ_SHARD = ITEMS.register("quartz_shard", () -> new Item(new Item.Properties()));
	public static final PlatformRegistry.Entry<Item> NETHERITE_SLIVER = ITEMS.register("netherite_sliver", () -> new Item(new Item.Properties()));
	public static final PlatformRegistry.Entry<Item> AMATHYLLIS_PETAL = ITEMS.register("amathyllis_petal", () -> new Item(new Item.Properties()));

	public static void addItemsToTabs(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
		if (itemDisplayParameters.hasPermissions()) {
			output.accept(DEBUGGER.get());
		}
		output.accept(JOURNAL.get().getDefaultInstance());
		output.accept(SEED_ANALYZER.get());
		output.accept(CLIPPER.get());
		output.accept(MAGNIFYING_GLASS.get());
		output.accept(WOODEN_RAKE.get());
		output.accept(IRON_RAKE.get());
		output.accept(TROWEL.get());
		output.accept(WOODEN_CROP_STICKS.get());
		output.accept(IRON_CROP_STICKS.get());
		output.accept(OBSIDIAN_CROP_STICKS.get());
		output.accept(SEED_BAG.get());

		output.accept(COAL_PEBBLE.get());
		output.accept(COPPER_NUGGET.get());
		output.accept(DIAMOND_SHARD.get());
		output.accept(EMERALD_SHARD.get());
		output.accept(QUARTZ_SHARD.get());
		output.accept(NETHERITE_SLIVER.get());
		output.accept(AMATHYLLIS_PETAL.get());
	}

}
