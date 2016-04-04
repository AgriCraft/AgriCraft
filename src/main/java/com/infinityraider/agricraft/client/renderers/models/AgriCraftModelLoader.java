/*
 * 
 */
package com.infinityraider.agricraft.client.renderers.models;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author RlonRyan
 */
@SideOnly(Side.CLIENT)
public final class AgriCraftModelLoader implements ICustomModelLoader {

	public static final AgriCraftModelLoader INSTANCE = new AgriCraftModelLoader();

	public static final String MODEL_ITEM = "agricraftitem:models/item/";

	private static final Set<String> DUMMY_MODELS = new HashSet<>();

	private AgriCraftModelLoader() {
		// NOP
	}

	synchronized public void addDummyModel(String model) {
		DUMMY_MODELS.add(model);
		//LogHelper.debug("Added: " + model + " to DNR list.");
	}

	@Override
	public boolean accepts(ResourceLocation loc) {
		return loc.toString().startsWith(MODEL_ITEM) || DUMMY_MODELS.contains(loc.toString());
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws IOException {

		// Return Dummy
		if (DUMMY_MODELS.contains(modelLocation.toString())) {
			return ModelLoaderRegistry.getMissingModel();
		}

		// Get the Textures.
		final String[] tex = modelLocation.toString().substring(MODEL_ITEM.length()).split("\\$");

		// Log
		//LogHelper.debug("Creating Item Model: With Textures: " + Arrays.toString(tex));
		
		// Create the model.
		final IModel model = new AgriCraftItemModel(tex);

		// Return the model.
		return model;

	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		// NOP
	}

}
