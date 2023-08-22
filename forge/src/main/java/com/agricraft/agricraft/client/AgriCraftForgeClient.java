package com.agricraft.agricraft.client;

import com.agricraft.agricraft.AgriCraft;
import com.agricraft.agricraft.client.ber.CropBlockEntityRenderer;
import com.agricraft.agricraft.client.gui.MagnifyingGlassOverlay;
import com.agricraft.agricraft.common.config.forge.ForgeMenuConfig;
import com.agricraft.agricraft.common.registry.ModBlockEntityTypes;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.checkerframework.checker.units.qual.C;

import java.util.Map;

@Mod.EventBusSubscriber(modid = AgriCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AgriCraftForgeClient {

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
	}

	@SubscribeEvent
	public static void registerBer(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ModBlockEntityTypes.CROP.get(), CropBlockEntityRenderer::new);
	}

	@SubscribeEvent
	public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
		event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "magnifying_glass_info", (forgeGui, guiGraphics, partialTicks, width, height) -> MagnifyingGlassOverlay.renderOverlay(guiGraphics, partialTicks));
	}

	public static void init() {
		ForgeMenuConfig.register();

	}

}
