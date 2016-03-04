/*
 * 
 */
package com.infinityraider.agricraft.models.loaders;

import com.infinityraider.agricraft.utility.LogHelper;
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
 * @author ryeni
 */
//@SideOnly(Side.CLIENT)
public final class AgriCraftDummyModelLoader implements ICustomModelLoader {
	
	public static final AgriCraftDummyModelLoader INSTANCE = new AgriCraftDummyModelLoader();
	
	private final Set<String> models;

	private AgriCraftDummyModelLoader() {
		this.models = new HashSet<>();
	}
	
	synchronized public void addModel(String model) {
		this.models.add(model);
		LogHelper.debug("Added: " + model + " to DNR list.");
	}
 
	@Override
	public boolean accepts(ResourceLocation modelLocation) {
		return this.models.contains(modelLocation.toString());
	}

	@Override
	public IModel loadModel(ResourceLocation modelLocation) throws IOException {

		// Return the model.
		return ModelLoaderRegistry.getMissingModel();

	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		// NOP
	}
	
}
