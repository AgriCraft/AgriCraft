package com.agricraft.agricraft.common.registry;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.common.item.DebuggerItem;
import com.agricraft.agricraft.common.item.RakeItem;
import com.agricraft.agricraft.common.util.PlatformUtils;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.List;

public class ModItems {
	public static final ResourcefulRegistry<Item> ITEMS = ResourcefulRegistries.create(BuiltInRegistries.ITEM, AgriCraft.MOD_ID);

	public static final RegistryEntry<Item> DEBUGGER = ITEMS.register("debugger", () -> new DebuggerItem(new Item.Properties()));

	public static final RegistryEntry<Item> RAKE_WOOD = ITEMS.register("rake_wood", () -> new RakeItem(new Item.Properties(), RakeItem.WOOD_LOGIC));
	public static final RegistryEntry<Item> RAKE_IRON = ITEMS.register("rake_iron", () -> new RakeItem(new Item.Properties(), RakeItem.IRON_LOGIC));
	public static final RegistryEntry<Item> SEED = ITEMS.register("seed", () -> PlatformUtils.createAgriSeedItem(new Item.Properties()));


	public static void onRegisterCreativeTabs(TriConsumer<ResourceLocation, RegistryEntry<Item>, List<Item>> consumer) {
		consumer.accept(new ResourceLocation(AgriCraft.MOD_ID, "main"), ModItems.DEBUGGER, List.of(DEBUGGER.get(), RAKE_WOOD.get(), RAKE_IRON.get()));
	}
}
