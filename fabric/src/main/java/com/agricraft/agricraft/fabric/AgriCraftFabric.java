package com.agricraft.agricraft.fabric;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriMutation;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.config.CompatConfig;
import com.agricraft.agricraft.api.config.CoreConfig;
import com.agricraft.agricraft.api.fertilizer.AgriFertilizer;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.plant.AgriWeed;
import com.agricraft.agricraft.common.commands.DumpRegistriesCommand;
import com.agricraft.agricraft.common.commands.GiveSeedCommand;
import com.agricraft.agricraft.common.handler.VanillaSeedConversion;
import com.agricraft.agricraft.common.plugin.FabricSeasonPlugin;
import com.agricraft.agricraft.common.util.Platform;
import com.agricraft.agricraft.common.util.fabric.FabricPlatform;
import com.agricraft.agricraft.compat.botania.BotaniaPlugin;
import com.agricraft.agricraft.compat.botania.ManaGrowthCondition;
import com.agricraft.agricraft.plugin.minecraft.MinecraftPlugin;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import vazkii.botania.api.mana.ManaBlockType;
import vazkii.botania.api.mana.ManaNetworkAction;
import vazkii.botania.api.mana.ManaNetworkCallback;

public class AgriCraftFabric implements ModInitializer {

	public static MinecraftServer cachedServer;

	@Override
	public void onInitialize() {
		Platform.setup(new FabricPlatform());
		AgriCraft.init();
		DynamicRegistries.registerSynced(AgriApi.AGRIPLANTS, AgriPlant.CODEC, AgriPlant.CODEC);
		DynamicRegistries.registerSynced(AgriApi.AGRIWEEDS, AgriWeed.CODEC, AgriWeed.CODEC);
		DynamicRegistries.registerSynced(AgriApi.AGRISOILS, AgriSoil.CODEC, AgriSoil.CODEC);
		DynamicRegistries.registerSynced(AgriApi.AGRIMUTATIONS, AgriMutation.CODEC, AgriMutation.CODEC);
		DynamicRegistries.registerSynced(AgriApi.AGRIFERTILIZERS, AgriFertilizer.CODEC, AgriFertilizer.CODEC);
		ServerLifecycleEvents.SERVER_STARTING.register(server -> cachedServer = server);
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			GiveSeedCommand.register(dispatcher, registryAccess);
			DumpRegistriesCommand.register(dispatcher, registryAccess);
		});
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> VanillaSeedConversion.onRightClick(player, hand, hitResult.getBlockPos(), hitResult));
		MinecraftPlugin.init();
		FabricSeasonPlugin.init();

		FabricLoader.getInstance().getModContainer("agricraft").ifPresent(agricraft -> {
			for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
				String modid = mod.getMetadata().getId();
				if (!modid.equals("minecraft") && !modid.equals("agricraft")) {
					// let's not use Fabric API internals and say we did!
					ResourceManagerHelper.registerBuiltinResourcePack(new ResourceLocation("builtin", "agricraft_resourcepacks_" + modid), "resourcepacks/" + modid, agricraft, CoreConfig.enablePacksByDefault);
					ResourceManagerHelper.registerBuiltinResourcePack(new ResourceLocation("builtin", "agricraft_datapacks_" + modid), "datapacks/" + modid, agricraft, CoreConfig.enablePacksByDefault);
				}
			}
		});
		if (FabricLoader.getInstance().isModLoaded("botania") && CompatConfig.enableBotania) {
			BotaniaPlugin.init();
			ManaNetworkCallback.EVENT.register((manaReceiver, manaBlockType, manaNetworkAction) -> {
				if (manaNetworkAction == ManaNetworkAction.REMOVE && manaBlockType == ManaBlockType.POOL) {
					ManaGrowthCondition.removePoll(manaReceiver);
				}
			});
		}
	}

}
