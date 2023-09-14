package com.agricraft.agricraft.fabric;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.common.commands.GiveSeedCommand;
import com.agricraft.agricraft.plugin.minecraft.MinecraftPlugin;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.server.MinecraftServer;

public class AgriCraftFabric implements ModInitializer {

	public static MinecraftServer cachedServer;

	@Override
	public void onInitialize() {
		AgriCraft.init();
		DynamicRegistries.registerSynced(AgriApi.AGRIPLANTS, AgriPlant.CODEC, AgriPlant.CODEC);
		DynamicRegistries.registerSynced(AgriApi.AGRISOILS, AgriSoil.CODEC, AgriSoil.CODEC);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> cachedServer = player.getServer());
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			GiveSeedCommand.register(dispatcher, registryAccess);
		});
		MinecraftPlugin.init();
	}

}
