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
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforgespi.language.IModInfo;

import java.io.IOException;
import java.nio.file.Path;

@Mod(AgriApi.MOD_ID)
public class AgriCraftNeoForge {

	public AgriCraftNeoForge(IEventBus bus) {
		Platform.setup(new NeoForgePlatform());
		AgriCraft.init();
		bus.addListener(AgriCraftNeoForge::onCommonSetup);
		bus.addListener(AgriCraftNeoForge::onRegisterDatapackRegistry);
		bus.addListener(AgriCraftNeoForge::onAddPackFinders);
		NeoForge.EVENT_BUS.addListener(AgriCraftNeoForge::onRegisterCommands);
		NeoForge.EVENT_BUS.addListener(AgriCraftNeoForge::onRightClick);
	}

	public static void onCommonSetup(FMLCommonSetupEvent event) {
		MinecraftPlugin.init();
//		SereneSeasonPlugin.init();
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

	public static void onAddPackFinders(AddPackFindersEvent event) {
		if (event.getPackType() == PackType.SERVER_DATA) {
			for (IModInfo mod : ModList.get().getMods()) {
				addPack("datapacks", mod.getModId(), PackType.SERVER_DATA, event);
			}
		}
		if (event.getPackType() == PackType.CLIENT_RESOURCES) {
			for (IModInfo mod : ModList.get().getMods()) {
				addPack("resourcepacks", mod.getModId(), PackType.CLIENT_RESOURCES, event);
			}
		}
	}

	public static void addPack(String type, String modid, PackType packType, AddPackFindersEvent event) {
		Path resourcePath = ModList.get().getModFileById(AgriApi.MOD_ID).getFile().findResource(type, modid);
		String id = "builtin/agricraft_" + type + "_" + modid;
		Pack.ResourcesSupplier resources = BuiltInPackSource.fromName(path -> new PathPackResources(path, resourcePath, true));
		try (PackResources packresources = resources.openPrimary(id)) {
			PackMetadataSection packmetadatasection = packresources.getMetadataSection(PackMetadataSection.TYPE);
			if (packmetadatasection == null) {
				return;
			}
		} catch (IOException ignored) {
		}
		Pack pack = Pack.readMetaAndCreate(id, Component.translatable("agricraft." + type + "." + modid), false, resources, packType, Pack.Position.TOP, PackSource.BUILT_IN);
		if (pack != null) {
			event.addRepositorySource(packConsumer -> packConsumer.accept(pack));
		}
	}

}
