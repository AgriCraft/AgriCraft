package com.agricraft.agricraft.neoforge;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriMutation;
import com.agricraft.agricraft.api.fertilizer.AgriFertilizer;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.plant.AgriWeed;
import com.agricraft.agricraft.common.commands.DumpRegistriesCommand;
import com.agricraft.agricraft.common.commands.GiveSeedCommand;
import com.agricraft.agricraft.common.handler.VanillaSeedConversion;
import com.agricraft.agricraft.common.plugin.SereneSeasonPlugin;
import com.agricraft.agricraft.common.util.Platform;
import com.agricraft.agricraft.common.util.forge.NeoForgePlatform;
import com.agricraft.agricraft.plugin.minecraft.MinecraftPlugin;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@Mod(AgriApi.MOD_ID)
public class AgriCraftNeoForge {

	public AgriCraftNeoForge(IEventBus bus) {
		Platform.setup(new NeoForgePlatform());
		AgriCraft.init();
		bus.addListener(AgriCraftNeoForge::onCommonSetup);
		bus.addListener(AgriCraftNeoForge::onRegisterDatapackRegistry);
		NeoForge.EVENT_BUS.addListener(AgriCraftNeoForge::onRegisterCommands);
		NeoForge.EVENT_BUS.addListener(AgriCraftNeoForge::onRightClick);
	}

	public static void onCommonSetup(FMLCommonSetupEvent event) {
		MinecraftPlugin.init();
		SereneSeasonPlugin.init();
	}

	public static void onRegisterDatapackRegistry(DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(AgriApi.AGRIPLANTS, AgriPlant.CODEC, AgriPlant.CODEC);
		event.dataPackRegistry(AgriApi.AGRIWEEDS, AgriWeed.CODEC, AgriWeed.CODEC);
		event.dataPackRegistry(AgriApi.AGRISOILS, AgriSoil.CODEC, AgriSoil.CODEC);
		event.dataPackRegistry(AgriApi.AGRIMUTATIONS, AgriMutation.CODEC, AgriMutation.CODEC);
		event.dataPackRegistry(AgriApi.AGRIFERTILIZERS, AgriFertilizer.CODEC, AgriFertilizer.CODEC);
	}

	public static void onRegisterCommands(RegisterCommandsEvent event) {
		GiveSeedCommand.register(event.getDispatcher(), event.getBuildContext());
		DumpRegistriesCommand.register(event.getDispatcher(), event.getBuildContext());
	}

	public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		if (VanillaSeedConversion.onRightClick(event.getEntity(), event.getHand(), event.getPos(), event.getHitVec())) {
			event.setUseItem(Event.Result.DENY);
			event.setCanceled(true);
		}
	}

}
