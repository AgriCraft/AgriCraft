package com.agricraft.agricraft.client.neoforge;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.client.AgriCraftClient;
import com.agricraft.agricraft.client.ber.CropBlockEntityRenderer;
import com.agricraft.agricraft.client.ber.SeedAnalyzerEntityRenderer;
import com.agricraft.agricraft.client.gui.MagnifyingGlassOverlay;
import com.agricraft.agricraft.client.gui.SeedAnalyzerScreen;
import com.agricraft.agricraft.common.config.forge.ForgeMenuConfig;
import com.agricraft.agricraft.common.registry.ModBlockEntityTypes;
import com.agricraft.agricraft.common.registry.ModBlocks;
import com.agricraft.agricraft.common.registry.ModMenus;
import com.agricraft.agricraft.common.util.PlatformClient;
import com.agricraft.agricraft.common.util.forge.NeoForgePlatformClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;

import java.util.Map;

/**
 * NeoForge client event handler in the mod event bus
 */
@Mod.EventBusSubscriber(modid = AgriApi.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AgriCraftNeoForgeClient {

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		PlatformClient.setup(new NeoForgePlatformClient());
		AgriCraftNeoForgeClient.init();
		MenuScreens.register(ModMenus.SEED_ANALYZER_MENU.get(), SeedAnalyzerScreen::new);
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.SEED_ANALYZER.get(), RenderType.cutout());
	}

	@SubscribeEvent
	public static void loadModels(ModelEvent.RegisterAdditional event) {
		// https://discord.com/channels/313125603924639766/983834532904042537/1104441106248253592
		for (Map.Entry<ResourceLocation, Resource> entry : FileToIdConverter.json("models/seed").listMatchingResources(Minecraft.getInstance().getResourceManager()).entrySet()) {
			ResourceLocation seed = new ResourceLocation(entry.getKey().toString().replace("models/seed", "seed").replace(".json", ""));
			event.register(seed);
		}
		for (Map.Entry<ResourceLocation, Resource> entry : FileToIdConverter.json("models/crop").listMatchingResources(Minecraft.getInstance().getResourceManager()).entrySet()) {
			ResourceLocation seed = new ResourceLocation(entry.getKey().toString().replace("models/crop", "crop").replace(".json", ""));
			event.register(seed);
		}
		for (Map.Entry<ResourceLocation, Resource> entry : FileToIdConverter.json("models/weed").listMatchingResources(Minecraft.getInstance().getResourceManager()).entrySet()) {
			ResourceLocation seed = new ResourceLocation(entry.getKey().toString().replace("models/weed", "weed").replace(".json", ""));
			event.register(seed);
		}
		// add the crop sticks models else they're not loaded
		event.register(new ResourceLocation("agricraft:block/wooden_crop_sticks"));
		event.register(new ResourceLocation("agricraft:block/iron_crop_sticks"));
		event.register(new ResourceLocation("agricraft:block/obsidian_crop_sticks"));
		event.register(new ResourceLocation("agricraft:block/wooden_cross_crop_sticks"));
		event.register(new ResourceLocation("agricraft:block/iron_cross_crop_sticks"));
		event.register(new ResourceLocation("agricraft:block/obsidian_cross_crop_sticks"));
	}

	@SubscribeEvent
	public static void registerBer(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ModBlockEntityTypes.CROP.get(), CropBlockEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntityTypes.SEED_ANALYZER.get(), SeedAnalyzerEntityRenderer::new);
	}

	@SubscribeEvent
	public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
		event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), new ResourceLocation(AgriApi.MOD_ID, "magnifying_glass_info"), (forgeGui, guiGraphics, partialTicks, width, height) -> MagnifyingGlassOverlay.renderOverlay(guiGraphics, partialTicks));
	}

	public static void init() {
		AgriCraftClient.init();
		ForgeMenuConfig.register();
	}

}
