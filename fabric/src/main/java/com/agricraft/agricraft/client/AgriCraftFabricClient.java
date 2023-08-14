package com.agricraft.agricraft.client;

import com.agricraft.agricraft.client.bewlr.AgriSeedBEWLR;
import com.agricraft.agricraft.common.registry.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
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
		});
	}

}
