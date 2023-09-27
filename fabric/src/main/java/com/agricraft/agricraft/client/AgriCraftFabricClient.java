package com.agricraft.agricraft.client;

import com.agricraft.agricraft.client.ber.CropBlockEntityRenderer;
import com.agricraft.agricraft.client.bewlr.AgriSeedBEWLR;
import com.agricraft.agricraft.client.gui.MagnifyingGlassOverlay;
import com.agricraft.agricraft.common.registry.ModBlockEntityTypes;
import com.agricraft.agricraft.common.registry.ModItems;
import com.mojang.datafixers.util.Either;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.util.Map;

public class AgriCraftFabricClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BuiltinItemRendererRegistry.INSTANCE.register(ModItems.SEED.get(), AgriSeedBEWLR.INSTANCE::renderByItem);
		ModelLoadingPlugin.register(pluginContext -> {
			for (Map.Entry<ResourceLocation, Resource> entry : FileToIdConverter.json("models/seed").listMatchingResources(Minecraft.getInstance().getResourceManager()).entrySet()) {
				ResourceLocation seed = new ResourceLocation(entry.getKey().toString().replace("models/seed", "seed").replace(".json", ""));
				pluginContext.addModels(seed);
			}
			for (Map.Entry<ResourceLocation, Resource> entry : FileToIdConverter.json("models/crop").listMatchingResources(Minecraft.getInstance().getResourceManager()).entrySet()) {
				ResourceLocation seed = new ResourceLocation(entry.getKey().toString().replace("models/crop", "crop").replace(".json", ""));
				pluginContext.addModels(seed);
			}
			// add the crop sticks models else they're not loaded
			pluginContext.addModels(new ResourceLocation("agricraft:block/wooden_crop_sticks"), new ResourceLocation("agricraft:block/iron_crop_sticks"), new ResourceLocation("agricraft:block/obsidian_crop_sticks"),
					new ResourceLocation("agricraft:block/wooden_cross_crop_sticks"), new ResourceLocation("agricraft:block/iron_cross_crop_sticks"), new ResourceLocation("agricraft:block/obsidian_cross_crop_sticks"));
		});

		BlockEntityRenderers.register(ModBlockEntityTypes.CROP.get(), CropBlockEntityRenderer::new);

		HudRenderCallback.EVENT.register((guiGraphics, partialTicks) -> {
			MagnifyingGlassOverlay.renderOverlay(guiGraphics, partialTicks);
		});
		ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
			if (stack.hasTag() && stack.getTag().getBoolean("magnifying")) {
				lines.add(1, Component.translatable("agricraft.tooltip.magnifying").withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));
			}
		});
	}

}
