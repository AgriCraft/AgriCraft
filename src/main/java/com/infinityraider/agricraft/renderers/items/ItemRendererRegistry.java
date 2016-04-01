package com.infinityraider.agricraft.renderers.items;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.items.ICustomRenderedItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ItemRendererRegistry implements ICustomModelLoader {
    private static final ItemRendererRegistry INSTANCE = new ItemRendererRegistry();

    public static ItemRendererRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<ResourceLocation, ItemRenderer<? extends Item>> renderers;
    private final List<ICustomRenderedItem<? extends Item>> items;

    private ItemRendererRegistry() {
        this.renderers = new HashMap<>();
        this.items = new ArrayList<>();
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
    public void onResourceManagerReload(IResourceManager resourceManager) {}

    public List<ICustomRenderedItem<? extends Item>> getRegisteredItems() {
        return ImmutableList.copyOf(items);
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void registerCustomItemRenderer(ICustomRenderedItem<? extends Item> customRenderedItem) {
        if (customRenderedItem == null || !(customRenderedItem instanceof Item)) {
            return;
        }
        Item item = (Item) customRenderedItem;
        ModelResourceLocation itemModel = customRenderedItem.getItemModelResourceLocation();
        IItemRenderingHandler<? extends Item> renderer = customRenderedItem.getRenderer();
        if (renderer != null) {
            ItemRenderer<? extends Item> instance = new ItemRenderer<>(renderer);
            renderers.put(itemModel, instance);
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, stack -> itemModel);

        }
        items.add(customRenderedItem);
    }
}
