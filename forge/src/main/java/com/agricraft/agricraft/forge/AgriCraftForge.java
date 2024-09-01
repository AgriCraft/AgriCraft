package com.agricraft.agricraft.forge;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriMutation;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.fertilizer.AgriFertilizer;
import com.agricraft.agricraft.api.plant.AgriPlant;
import com.agricraft.agricraft.api.plant.AgriWeed;
import com.agricraft.agricraft.common.commands.DumpRegistriesCommand;
import com.agricraft.agricraft.common.commands.GiveSeedCommand;
import com.agricraft.agricraft.common.handler.VanillaSeedConversion;
import com.agricraft.agricraft.common.util.Platform;
import com.agricraft.agricraft.plugin.minecraft.MinecraftPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.DataPackRegistryEvent;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;

@Mod(AgriApi.MOD_ID)
public class AgriCraftForge {

	public AgriCraftForge() {
		AgriCraft.init();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(AgriCraftForge::onCommonSetup);
		bus.addListener(AgriCraftForge::onRegisterDatapackRegistry);
		bus.addListener(AgriCraftForge::onAddPackFinders);
		MinecraftForge.EVENT_BUS.addListener(AgriCraftForge::onRegisterCommands);
		MinecraftForge.EVENT_BUS.addListener(AgriCraftForge::onRightClick);
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
				String modId = mod.getModId();
				if (!modId.equals("minecraft") && !modId.equals("agricraft")) {
					addPack("datapacks", modId, PackType.SERVER_DATA, event);
				}
			}
		}
		if (event.getPackType() == PackType.CLIENT_RESOURCES) {
			for (IModInfo mod : ModList.get().getMods()) {
				String modId = mod.getModId();
				if (!modId.equals("minecraft") && !modId.equals("agricraft")) {
					addPack("resourcepacks", modId, PackType.CLIENT_RESOURCES, event);
				}
			}
		}
	}

	public static void addPack(String type, String modid, PackType packType, AddPackFindersEvent event) {
		Path resourcePath = ModList.get().getModFileById(AgriApi.MOD_ID).getFile().findResource(type, modid);
		String id = "builtin/agricraft_" + type + "_" + modid;
		Function<String, PackResources> onName = path -> new PathPackResources(path, resourcePath, true);
		Pack.ResourcesSupplier resources = new Pack.ResourcesSupplier() {
			@Override
			public PackResources open(String string) {
				return onName.apply(id);
			}
		};
		try (PackResources packresources = resources.open(id)) {
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
