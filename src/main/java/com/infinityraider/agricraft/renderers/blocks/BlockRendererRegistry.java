package com.infinityraider.agricraft.renderers.blocks;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.blocks.ICustomRenderedBlock;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class BlockRendererRegistry implements ICustomModelLoader {
    private static final BlockRendererRegistry INSTANCE = new BlockRendererRegistry();

    public static BlockRendererRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<ResourceLocation, BlockRenderer<? extends TileEntity>> renderers;
    private final List<ICustomRenderedBlock<? extends TileEntity>> blocks;

    private BlockRendererRegistry() {
        this.renderers = new HashMap<>();
        this.blocks = new ArrayList<>();
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

    public List<ICustomRenderedBlock<? extends TileEntity>> getRegisteredBlocks() {
        return ImmutableList.copyOf(blocks);
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void registerCustomBlockRenderer(ICustomRenderedBlock block) {
        IBlockRenderingHandler renderer = block.getRenderer();
        if(renderer != null) {
            BlockRenderer instance = new BlockRenderer<>(renderer);
            if(renderer.hasStaticRendering()) {
                renderers.put(block.getBlockModelResourceLocation(), instance);
            }
            TileEntity tile = renderer.getTileEntity();
            if(renderer.hasDynamicRendering() && tile != null) {
                ClientRegistry.bindTileEntitySpecialRenderer(tile.getClass(), instance);
            }
            blocks.add(block);
        }
    }
}
