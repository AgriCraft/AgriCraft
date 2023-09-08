package com.agricraft.agricraft.forge;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.client.forge.AgriCraftForgeClient;
import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.api.codecs.AgriSeed;
import com.agricraft.agricraft.common.commands.GiveSeedCommand;
import com.agricraft.agricraft.common.config.CoreConfig;
import com.agricraft.agricraft.common.registry.ModItems;
import com.agricraft.agricraft.common.util.PlatformUtils;
import com.agricraft.agricraft.plugin.minecraft.MinecraftPlugin;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DataPackRegistryEvent;

@Mod(AgriCraft.MOD_ID)
public class AgriCraftForge {

	public AgriCraftForge() {
		AgriCraft.init();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(AgriCraftForge::onCommonSetup);
		bus.addListener(AgriCraftForge::onRegisterDatapackRegistry);
		MinecraftForge.EVENT_BUS.addListener(AgriCraftForge::onRegisterCommands);
		if (FMLEnvironment.dist.isClient()) {
			AgriCraftForgeClient.init();
		}
	}

	public static void onCommonSetup(FMLCommonSetupEvent event) {
		MinecraftPlugin.init();
	}

	public static void onRegisterDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(PlatformUtils.getPlantRegistryKey(), AgriPlant.CODEC, AgriPlant.CODEC);
		event.dataPackRegistry(PlatformUtils.AGRISOILS, AgriSoil.CODEC, AgriSoil.CODEC);
	}

	public static void onRegisterCommands(RegisterCommandsEvent event) {
		GiveSeedCommand.register(event.getDispatcher(), event.getBuildContext());
	}

}
