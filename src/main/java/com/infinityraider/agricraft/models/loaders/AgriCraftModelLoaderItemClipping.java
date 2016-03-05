/*
 * 
 */
package com.infinityraider.agricraft.models.loaders;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;

/**
 *
 * @author RlonRyan
 */
//@SideOnly(Side.CLIENT)
public class AgriCraftModelLoaderItemClipping implements ICustomModelLoader {
	
	public static final String MODEL_DOMAIN = "agricraftclipping:models/item/";
	
	public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("agricraft:items/clipping");

	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		return modelLocation.toString().startsWith(MODEL_DOMAIN);
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws IOException {

		// Get Key
		final String key = modelLocation.toString();
		
		// Prep. the name
		final String tex = key.substring(MODEL_DOMAIN.length()).replaceFirst("/", ":");

		// Log
		// LogHelper.debug("Creating Item Model With Texture: " + tex);

		// Create the model.
		final ImmutableList.Builder<ResourceLocation> textures = new ImmutableList.Builder<>();
		textures.add(BACKGROUND_TEXTURE);
		textures.add(new ResourceLocation(tex));
		final ItemLayerModel model = new ItemLayerModel(textures.build());

		// Return the model.
		return model;

	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		// NOP
	}
	
}
