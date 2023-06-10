package com.agricraft.agricraft.forge;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.client.AgriCraftForgeClient;
import com.agricraft.agricraft.common.codecs.AgriSeed;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.agricraft.agricraft.common.registry.ModItems;
import com.agricraft.agricraft.common.registry.ModSeeds;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Map;

@Mod(AgriCraft.MOD_ID)
public class AgriCraftForge {

	public AgriCraftForge() {
		AgriCraft.init();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(AgriCraftForge::onClientSetup);
		bus.addListener(AgriCraftForge::onRegisterCreativeTabs);
		bus.addListener(AgriCraftForge::onRegisterDatapackRegistry);
	}

	public static void onClientSetup(FMLClientSetupEvent event) {
	}

	public static CreativeModeTab MAIN_TAB;
	public static CreativeModeTab SEED_TAB;

	@SubscribeEvent
	public static void onRegisterCreativeTabs(CreativeModeTabEvent.Register event) {
		AgriCraft.LOGGER.info("register tab event");
		MAIN_TAB = event.registerCreativeModeTab(new ResourceLocation(AgriCraft.MOD_ID, "main"), builder -> builder
				.icon(() -> new ItemStack(ModItems.DEBUGGER.get()))
				.title(Component.translatable("itemGroup.agricraft.main"))
				.displayItems((itemDisplayParameters, output) -> {
					AgriCraft.LOGGER.info("register item in tab");
					output.accept(ModItems.DEBUGGER.get());
					output.accept(ModItems.RAKE_WOOD.get());
					output.accept(ModItems.RAKE_IRON.get());
					output.accept(ModItems.SEED.get());
				})
				.build());
		SEED_TAB = event.registerCreativeModeTab(new ResourceLocation(AgriCraft.MOD_ID, "seeds"), builder -> builder
				.title(Component.translatable("itemGroup.agricraft.seeds"))
				.icon(() -> ModItems.SEED.get().getDefaultInstance())
				.displayItems((itemDisplayParameters, output) -> {
					Registry<AgriSeed> registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registry(ModSeeds.AGRISEEDS).get();
					for (Map.Entry<ResourceKey<AgriSeed>, AgriSeed> entry : registry.entrySet()) {
						output.accept(AgriSeedItem.toStack(entry.getKey().location().toString()));
					}
				})
				.build());
	}

	@SubscribeEvent
	public static void onRegisterDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(ModSeeds.AGRISEEDS, AgriSeed.CODEC, AgriSeed.CODEC);
	}

}
