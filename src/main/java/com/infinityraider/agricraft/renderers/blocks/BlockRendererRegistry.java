package com.infinityraider.agricraft.renderers.blocks;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
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

    private final Map<ResourceLocation, BlockRenderer> renderers;
    private final List<ICustomRenderedBlock> blocks;

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

    public List<ICustomRenderedBlock> getRegisteredBlocks() {
        return ImmutableList.copyOf(blocks);
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void registerCustomBlockRenderer(ICustomRenderedBlock customRenderedBlock) {
        if (customRenderedBlock == null || !(customRenderedBlock instanceof Block)) {
            return;
        }
        Block block = (Block) customRenderedBlock;
        IBlockRenderingHandler renderer = customRenderedBlock.getRenderer();
        //set custom state mapper
        StateMapperBase stateMapper = new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return customRenderedBlock.getBlockModelResourceLocation();
            }
        };
        //register renderers
        ModelLoader.setCustomStateMapper(block, stateMapper);
        if (renderer != null) {
            BlockRenderer instance = new BlockRenderer<>(renderer);
            ModelResourceLocation blockModel = customRenderedBlock.getBlockModelResourceLocation();
            if (renderer.hasStaticRendering()) {
                renderers.put(blockModel, instance);
            }
            TileEntity tile = renderer.getTileEntity();
            if (renderer.hasDynamicRendering() && tile != null) {
                ClientRegistry.bindTileEntitySpecialRenderer(tile.getClass(), instance);
            }
            if (renderer.doInventoryRendering()) {
                ModelResourceLocation itemModel = new ModelResourceLocation(blockModel.getResourceDomain() + ":" + blockModel.getResourcePath(), "inventory");
                renderers.put(itemModel, instance);
                ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(block), stack -> itemModel);
            }
        }
        blocks.add(customRenderedBlock);
    }
}
