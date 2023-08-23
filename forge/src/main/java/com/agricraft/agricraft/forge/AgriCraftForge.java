package com.agricraft.agricraft.forge;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.client.forge.AgriCraftForgeClient;
import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.api.codecs.AgriSeed;
import com.agricraft.agricraft.common.commands.GiveSeedCommand;
import com.agricraft.agricraft.common.util.PlatformUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DataPackRegistryEvent;

@Mod(AgriCraft.MOD_ID)
public class AgriCraftForge {

	public AgriCraftForge() {
		AgriCraft.init();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
//		bus.addListener(AgriCraftForge::onClientSetup);
		bus.addListener(AgriCraftForge::onRegisterDatapackRegistry);
		MinecraftForge.EVENT_BUS.addListener(AgriCraftForge::onRegisterCommands);
		if (FMLEnvironment.dist.isClient()) {
			AgriCraftForgeClient.init();
		}
	}



//	@SubscribeEvent
	public static void onRegisterDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(PlatformUtils.getSeedRegistryKey(), AgriSeed.CODEC, AgriSeed.CODEC);
		event.dataPackRegistry(PlatformUtils.getPlantRegistryKey(), AgriPlant.CODEC, AgriPlant.CODEC);
	}

	public static void onRegisterCommands(RegisterCommandsEvent event) {
		GiveSeedCommand.register(event.getDispatcher(), event.getBuildContext());
	}
}
