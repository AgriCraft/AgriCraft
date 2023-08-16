package com.agricraft.agricraft.forge;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.client.AgriCraftForgeClient;
import com.agricraft.agricraft.common.codecs.AgriPlant;
import com.agricraft.agricraft.common.codecs.AgriSeed;
import com.agricraft.agricraft.common.util.PlatformUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DataPackRegistryEvent;

@Mod(AgriCraft.MOD_ID)
public class AgriCraftForge {

	public AgriCraftForge() {
		AgriCraft.init();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(AgriCraftForge::onClientSetup);
		bus.addListener(AgriCraftForge::onRegisterDatapackRegistry);
		if (FMLEnvironment.dist.isClient()) {
			AgriCraftForgeClient.init();
		}
	}

	public static void onClientSetup(FMLClientSetupEvent event) {

	}

	@SubscribeEvent
	public static void onRegisterDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(PlatformUtils.getSeedRegistryKey(), AgriSeed.CODEC, AgriSeed.CODEC);
		event.dataPackRegistry(PlatformUtils.getPlantRegistryKey(), AgriPlant.CODEC, AgriPlant.CODEC);
	}

}
