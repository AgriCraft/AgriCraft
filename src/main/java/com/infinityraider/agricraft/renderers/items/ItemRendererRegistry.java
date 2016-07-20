package com.infinityraider.agricraft.renderers.items;

import com.infinityraider.agricraft.renderers.items.IAutoRenderedItem;
import com.infinityraider.agricraft.renderers.items.IItemRenderingHandler;
import com.infinityraider.agricraft.renderers.items.RenderItemAuto;
import com.infinityraider.agricraft.renderers.items.ItemRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ItemRendererRegistry implements ICustomModelLoader {

	private static final ItemRendererRegistry INSTANCE = new ItemRendererRegistry();

	public static ItemRendererRegistry getInstance() {
		return INSTANCE;
	}

	private final Map<ResourceLocation, ItemRenderer> renderers;

	private ItemRendererRegistry() {
		this.renderers = new HashMap<>();
		ModelLoaderRegistry.registerLoader(this);
	}

	@Override
	public boolean accepts(ResourceLocation loc) {
		return renderers.containsKey(loc);
	}

	@Override
	public IModel loadModel(ResourceLocation loc) throws Exception {
		return renderers.get(loc);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
	}

	public void registerCustomItemRenderer(Item item, IItemRenderingHandler handler) {
		final ModelResourceLocation itemModel = new ModelResourceLocation(item.getRegistryName(), "inventory");
		final ItemRenderer instance = new ItemRenderer(handler);
		ModelLoader.setCustomMeshDefinition(item, stack -> itemModel);
		renderers.put(itemModel, instance);
	}

	public <T extends Item & IAutoRenderedItem> void registerCustomItemRendererAuto(T item) {
		final ModelResourceLocation itemModel = new ModelResourceLocation(item.getRegistryName(), "inventory");
		final ItemRenderer instance = new ItemRenderer(new RenderItemAuto(item));
		ModelLoader.setCustomMeshDefinition(item, stack -> itemModel);
		renderers.put(itemModel, instance);
	}

}
