package com.infinityraider.agricraft.renderers.renderinghacks;

import com.infinityraider.agricraft.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public final class BlockRendererDispatcherWrapped extends BlockRendererDispatcher implements IRenderingRegistry {
	
    private static BlockRendererDispatcherWrapped INSTANCE;

    private final BlockRendererDispatcher prevDispatcher;

    private final Map<Block, IRenderingHandler> blockRenderers;
    private final Map<Item, IRenderingHandler> itemRenderers;

    public static void init() {
        BlockRendererDispatcher prevDispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
        INSTANCE = new BlockRendererDispatcherWrapped(prevDispatcher, getGameSettings(prevDispatcher));
        applyDispatcher();
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(INSTANCE);
        RenderItemWrapped.init();
    }

    public static BlockRendererDispatcherWrapped getInstance() {
        return INSTANCE;
    }

    private BlockRendererDispatcherWrapped(BlockRendererDispatcher prevDispatcher, GameSettings settings) {
        super(prevDispatcher.getBlockModelShapes(), settings);
        this.prevDispatcher = prevDispatcher;
        blockRenderers = new HashMap<>();
        itemRenderers = new HashMap<>();
    }

    @Override
    public void registerBlockRenderingHandler(Block block, IRenderingHandler renderer) {
        blockRenderers.put(block, renderer);
    }

    @Override
    public void registerItemRenderingHandler(Item item, IRenderingHandler renderer) {
        itemRenderers.put(item, renderer);
    }

    @Override
    public IRenderingHandler getRenderingHandler(Block block) {
        return blockRenderers.get(block);
    }

    @Override
    public IRenderingHandler getItemRenderer(Item item) {
        return itemRenderers.get(item);
    }

    @Override
    public boolean hasRenderingHandler(Block block) {
        return blockRenderers.containsKey(block);
    }

    @Override
    public boolean hasRenderingHandler(Item item) {
        return itemRenderers.containsKey(item);
    }

    @Override
    public void renderBlockDamage(IBlockState state, BlockPos pos, TextureAtlasSprite texture, IBlockAccess blockAccess) {
        prevDispatcher.renderBlockDamage(state, pos, texture, blockAccess);
    }

    @Override
    public boolean renderBlock(IBlockState state, BlockPos pos, IBlockAccess world, WorldRenderer worldRendererIn) {
        Block block = state.getBlock();
        if(blockRenderers.containsKey(block)) {
            try {
                return blockRenderers.get(block).renderWorldBlock(world, pos.getX(), pos.getY(), pos.getZ(), pos, block, state, worldRendererIn);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block in world");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
                CrashReportCategory.addBlockInfo(crashreportcategory, pos, state.getBlock(), state.getBlock().getMetaFromState(state));
                throw new ReportedException(crashreport);
            }
        }
        return prevDispatcher.renderBlock(state, pos, world, worldRendererIn);
    }

    @Override
    public BlockModelRenderer getBlockModelRenderer() {
        return prevDispatcher.getBlockModelRenderer();
    }

    @Override
    public IBakedModel getModelFromBlockState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return prevDispatcher.getModelFromBlockState(state, worldIn, pos);
    }

    @Override
    public void renderBlockBrightness(IBlockState state, float brightness) {
        prevDispatcher.renderBlockBrightness(state, brightness);
    }

    @Override
    public boolean isRenderTypeChest(Block block, int meta) {
        return prevDispatcher.isRenderTypeChest(block, meta);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        prevDispatcher.onResourceManagerReload(resourceManager);
    }

    private static GameSettings getGameSettings(BlockRendererDispatcher dispatcher) {
        GameSettings settings = Minecraft.getMinecraft().gameSettings;
        for (Field field : dispatcher.getClass().getDeclaredFields()) {
            if (field.getType() == GameSettings.class) {
                field.setAccessible(true);
                try {
                    settings = (GameSettings) field.get(dispatcher);
                } catch (Exception e) {
                    LogHelper.printStackTrace(e);
                }
                field.setAccessible(false);
                break;
            }
            else if (field.getType() == BlockRendererDispatcher.class) {
                field.setAccessible(true);
                try {
                    //Recursive, in case someone wrapped the BlockRenderingDispatcher too
                    settings = getGameSettings((BlockRendererDispatcher) field.get(dispatcher));
                } catch (Exception e) {
                    LogHelper.printStackTrace(e);
                }
                field.setAccessible(false);
                break;
            }

        }
        return settings;
    }

    @SuppressWarnings("unchecked")
    private static void applyDispatcher() {
        Minecraft mc = Minecraft.getMinecraft();
        for(Field field:mc.getClass().getDeclaredFields()) {
            if(field.getType() == BlockRendererDispatcher.class) {
                field.setAccessible(true);
                try {
                    field.set(mc, INSTANCE);
                } catch (IllegalAccessException e) {
                    LogHelper.printStackTrace(e);
                }
                field.setAccessible(false);
                break;
            }
        }
        IResourceManager manager = mc.getResourceManager();
        for(Field field:manager.getClass().getDeclaredFields()) {
            if(field.getType() == List.class) {
                field.setAccessible(true);
                try {
                    List<IResourceManagerReloadListener> list = (List<IResourceManagerReloadListener>) field.get(manager);
                    Iterator<IResourceManagerReloadListener> it = list.iterator();
                    while(it.hasNext()) {
                        IResourceManagerReloadListener listener = it.next();
                        if(listener instanceof BlockRendererDispatcher) {
                            it.remove();
                        }
                    }
                } catch (Exception e) {
                    LogHelper.printStackTrace(e);
                }
                field.setAccessible(false);
            }
        }
    }
}
