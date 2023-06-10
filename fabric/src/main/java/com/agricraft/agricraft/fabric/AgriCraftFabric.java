package com.agricraft.agricraft.fabric;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.common.codecs.AgriSeed;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.agricraft.agricraft.common.registry.ModItems;
import com.agricraft.agricraft.common.registry.ModSeeds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class AgriCraftFabric implements ModInitializer {

	public static CreativeModeTab MAIN_TAB;
	public static CreativeModeTab SEED_TAB;


	public static MinecraftServer cachedServer;

	@Override
	public void onInitialize() {
		AgriCraft.init();
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> cachedServer = server);
		MappedRegistry<AgriSeed> agriSeedRegistry = FabricRegistryBuilder.createSimple(ModSeeds.AGRISEEDS).attribute(RegistryAttribute.MODDED).attribute(RegistryAttribute.SYNCED).buildAndRegister();

		MAIN_TAB = FabricItemGroup.builder(new ResourceLocation(AgriCraft.MOD_ID, "main"))
				.icon(() -> new ItemStack(ModItems.DEBUGGER.get()))
				.title(Component.translatable("itemGroup.agricraft.main"))
				.displayItems((itemDisplayParameters, output) -> {
					AgriCraft.LOGGER.info("register item in tab");
					output.accept(ModItems.DEBUGGER.get());
					output.accept(ModItems.RAKE_WOOD.get());
					output.accept(ModItems.RAKE_IRON.get());
					output.accept(ModItems.SEED.get());
				})
				.build();
		SEED_TAB = FabricItemGroup.builder(new ResourceLocation(AgriCraft.MOD_ID, "seeds"))
				.title(Component.translatable("itemGroup.agricraft.seeds"))
				.icon(() -> ModItems.SEED.get().getDefaultInstance())
				.displayItems((itemDisplayParameters, output) -> {
					if (cachedServer != null) {
						Registry<AgriSeed> registry = cachedServer.registryAccess().registry(ModSeeds.AGRISEEDS).get();
						for (Map.Entry<ResourceKey<AgriSeed>, AgriSeed> entry : registry.entrySet()) {
							output.accept(AgriSeedItem.toStack(entry.getKey().location().toString()));
						}
					}
				})
				.build();

	}

}
