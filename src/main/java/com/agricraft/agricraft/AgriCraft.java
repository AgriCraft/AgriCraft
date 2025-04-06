package com.agricraft.agricraft;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.config.AgriCraftConfig;
import com.agricraft.agricraft.common.commands.DumpRegistriesCommand;
import com.agricraft.agricraft.common.commands.GiveSeedCommand;
import com.agricraft.agricraft.common.handler.VanillaSeedConversion;
import com.agricraft.agricraft.common.registry.AgriRegistries;
import com.agricraft.agricraft.compat.sereneseason.SereneSeasonPlugin;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.language.IModInfo;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Mod(AgriApi.MOD_ID)
public class AgriCraft {

	public static final Logger LOGGER = LogUtils.getLogger();

	public AgriCraft(IEventBus bus, ModContainer modContainer) {
		AgriRegistries.register(bus);
		AgriApi.set(new AgriApiImpl());
		LOGGER.info("Intializing API for " + AgriApi.MOD_ID);
		bus.addListener(AgriCraft::onCommonSetup);
		bus.addListener(AgriCraft::onAddPackFinders);
		NeoForge.EVENT_BUS.addListener(AgriCraft::onRegisterCommands);
		NeoForge.EVENT_BUS.addListener(AgriCraft::onRightClick);
		modContainer.registerConfig(ModConfig.Type.COMMON, AgriCraftConfig.SPEC);
		if (FMLEnvironment.dist.isClient()) {
			modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		}
	}

	public static void onCommonSetup(FMLCommonSetupEvent event) {
		if (ModList.get().isLoaded(SereneSeasonPlugin.ID)) {
			SereneSeasonPlugin.init();
		}
	}

	public static void onRegisterCommands(RegisterCommandsEvent event) {
		GiveSeedCommand.register(event.getDispatcher(), event.getBuildContext());
		DumpRegistriesCommand.register(event.getDispatcher(), event.getBuildContext());
	}

	public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		if (VanillaSeedConversion.onRightClick(event.getEntity(), event.getHand(), event.getPos(), event.getHitVec())) {
			event.setUseItem(TriState.FALSE);
			event.setCanceled(true);
		}
	}

	public static void onAddPackFinders(AddPackFindersEvent event) {
		IModFileInfo agricraft = ModList.get().getModFileById(AgriApi.MOD_ID);
		if (event.getPackType() == PackType.SERVER_DATA) {
			for (IModInfo mod : ModList.get().getMods()) {
				addPack("datapacks", mod.getModId(), agricraft, PackType.SERVER_DATA, event);
			}
		}
		if (event.getPackType() == PackType.CLIENT_RESOURCES) {
			for (IModInfo mod : ModList.get().getMods()) {
				addPack("resourcepacks", mod.getModId(), agricraft, PackType.CLIENT_RESOURCES, event);
			}
		}
	}

	public static void addPack(String type, String modid, IModFileInfo agricraft, PackType packType, AddPackFindersEvent event) {
		PackLocationInfo packLocationInfo = new PackLocationInfo("mod/agricraft_" + type + "_" + modid,
				Component.translatable("agricraft." + type + "." + modid),
				PackSource.BUILT_IN,
				Optional.of(new KnownPack("agricraft", "mod/agricraft_" + type + "_" + modid, agricraft.versionString())));
		Path resourcePath = agricraft.getFile().findResource(type, modid);
		Pack.ResourcesSupplier resources = BuiltInPackSource.fromName(path -> new PathPackResources(path, resourcePath));
		try (PackResources packresources = resources.openPrimary(packLocationInfo)) {
			PackMetadataSection packmetadatasection = packresources.getMetadataSection(PackMetadataSection.TYPE);
			if (packmetadatasection == null) {
				return;
			}
		} catch (IOException ignored) {
		}
		Pack pack = Pack.readMetaAndCreate(packLocationInfo, resources, packType, new PackSelectionConfig(AgriCraftConfig.REGISTER_PACKS_BY_DEFAULT.get(), Pack.Position.TOP, false));
		if (pack != null) {
			event.addRepositorySource(packConsumer -> packConsumer.accept(pack));
		}
	}

}
