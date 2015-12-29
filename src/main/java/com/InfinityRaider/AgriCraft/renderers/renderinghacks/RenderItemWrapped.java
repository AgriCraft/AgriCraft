package com.InfinityRaider.AgriCraft.renderers.renderinghacks;

import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ReportedException;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;

@SideOnly(Side.CLIENT)
@SuppressWarnings("deprecation")
public final class RenderItemWrapped extends RenderItem {
    private static RenderItemWrapped INSTANCE;
    private static IRenderingRegistry renderingRegistry;

    private final RenderItem prevRenderItem;
    private final TextureManager textureManager;

    public static void init() {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        TextureManager textureManager = getTextureManager(renderItem);
        ModelManager modelManager = getModelManager();
        INSTANCE = new RenderItemWrapped(renderItem, textureManager, modelManager);
        applyRenderItem();
        resetRenderManagerEntries();
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(INSTANCE);
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(Minecraft.getMinecraft().entityRenderer);
        renderingRegistry = BlockRendererDispatcherWrapped.getInstance();
    }

    public static RenderItemWrapped getInstance() {
        return INSTANCE;
    }

    private RenderItemWrapped(RenderItem prevRenderItem, TextureManager textureManager, ModelManager modelManager) {
        super(textureManager, modelManager);
        this.prevRenderItem = prevRenderItem;
        this.textureManager = textureManager;
    }

    private void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        renderingRegistry.getItemRenderer(stack.getItem()).renderItem(stack, transformType);
    }

    private boolean isHandled(ItemStack stack) {
        return stack != null
                && stack.getItem() != null
                && renderingRegistry.hasRenderingHandler(stack.getItem());
    }

    @Override
    public void func_175039_a(boolean flag) {
        prevRenderItem.func_175039_a(flag);
    }

    @Override
    public void renderItem(ItemStack stack, IBakedModel model) {
        if(isHandled(stack)) {
            this.renderItemModelTransform(stack, model, ItemCameraTransforms.TransformType.NONE);
        } else {
            prevRenderItem.renderItem(stack, model);
        }
    }

    @Override
    public boolean shouldRenderItemIn3D(ItemStack stack) {
        return isHandled(stack) || prevRenderItem.shouldRenderItemIn3D(stack);
    }

    @Override
    public void func_181564_a(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        if (isHandled(stack)) {
            if(stack.getItem() != null && stack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock) stack.getItem()).block;
                if(renderingRegistry.hasRenderingHandler(block)) {
                    IBakedModel ibakedmodel = this.getItemModelMesher().getItemModel(stack);
                    this.renderItemModelTransform(stack, ibakedmodel, transformType);
                }
            }
        }
        else {
            prevRenderItem.func_181564_a(stack, transformType);
        }
    }

    @Override
    public void renderItemModelForEntity(ItemStack stack, EntityLivingBase entityToRenderFor, ItemCameraTransforms.TransformType cameraTransformType) {
        if(isHandled(stack)) {
            if (entityToRenderFor != null && entityToRenderFor instanceof EntityPlayer) {
                IBakedModel ibakedmodel = this.getItemModelMesher().getItemModel(stack);
                EntityPlayer entityplayer = (EntityPlayer)entityToRenderFor;
                Item item = stack.getItem();
                ModelResourceLocation modelresourcelocation = item.getModel(stack, entityplayer, entityplayer.getItemInUseCount());
                if (modelresourcelocation != null) {
                    ibakedmodel = this.getItemModelMesher().getModelManager().getModel(modelresourcelocation);
                }
                this.renderItemModelTransform(stack, ibakedmodel, cameraTransformType);
            }
        } else {
            prevRenderItem.renderItemModelForEntity(stack, entityToRenderFor, cameraTransformType);
        }
    }

    @Override
    protected void renderItemModelTransform(ItemStack stack, IBakedModel model, ItemCameraTransforms.TransformType cameraTransformType) {
        if(isHandled(stack)) {
            this.textureManager.bindTexture(TextureMap.locationBlocksTexture);
            this.textureManager.getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
            this.preTransform(stack);
            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();

            renderItem(stack, cameraTransformType);

            GlStateManager.cullFace(1029);
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            this.textureManager.bindTexture(TextureMap.locationBlocksTexture);
            this.textureManager.getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
        }
        else {
            prevRenderItem.renderItem(stack, model);
        }
    }

    private void preTransform(ItemStack stack) {
        IBakedModel ibakedmodel = this.getItemModelMesher().getItemModel(stack);
        Item item = stack.getItem();
        if (item != null) {
            boolean flag = ibakedmodel.isGui3d();
            if (!flag) {
                GlStateManager.scale(2.0F, 2.0F, 2.0F);
            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public void renderItemIntoGUI(ItemStack stack, int x, int y) {
        if(!isHandled(stack)) {
            prevRenderItem.renderItemIntoGUI(stack, x, y);
        } else {
            IBakedModel ibakedmodel = this.getItemModelMesher().getItemModel(stack);
            GlStateManager.pushMatrix();
            this.textureManager.bindTexture(TextureMap.locationBlocksTexture);
            this.textureManager.getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.setupGuiTransform(x, y, ibakedmodel.isGui3d());

            this.renderItem(stack, ItemCameraTransforms.TransformType.GUI);

            GlStateManager.disableAlpha();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableLighting();
            GlStateManager.popMatrix();
            this.textureManager.bindTexture(TextureMap.locationBlocksTexture);
            this.textureManager.getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
        }
    }

    private void setupGuiTransform(int xPosition, int yPosition, boolean isGui3d) {
        GlStateManager.translate((float)xPosition, (float)yPosition, 100.0F + this.zLevel);
        GlStateManager.translate(8.0F, 8.0F, 0.0F);
        GlStateManager.scale(1.0F, 1.0F, -1.0F);
        GlStateManager.scale(0.5F, 0.5F, 0.5F);

        if (isGui3d)
        {
            GlStateManager.scale(40.0F, 40.0F, 40.0F);
            GlStateManager.rotate(210.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.enableLighting();
        }
        else
        {
            GlStateManager.scale(64.0F, 64.0F, 64.0F);
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.disableLighting();
        }
    }

    @Override
    public void renderItemAndEffectIntoGUI(final ItemStack stack, int xPosition, int yPosition) {
        if(isHandled(stack)) {
            prevRenderItem.renderItemAndEffectIntoGUI(stack, xPosition, yPosition);
        } else {
            if (stack != null && stack.getItem() != null) {
                this.zLevel += 50.0F;
                try {
                    this.renderItemIntoGUI(stack, xPosition, yPosition);
                }
                catch (Throwable throwable) {
                    CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering item");
                    CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being rendered");
                    crashreportcategory.addCrashSectionCallable("Item Type", () -> String.valueOf(stack.getItem()));
                    crashreportcategory.addCrashSectionCallable("Item Aux", () -> String.valueOf(stack.getMetadata()));
                    crashreportcategory.addCrashSectionCallable("Item NBT", () -> String.valueOf(stack.getTagCompound()));
                    crashreportcategory.addCrashSectionCallable("Item Foil", () -> String.valueOf(stack.hasEffect()));
                    throw new ReportedException(crashreport);
                }
                this.zLevel -= 50.0F;
            }
        }
    }

    @Override
    public void renderItemOverlays(FontRenderer fr, ItemStack stack, int xPosition, int yPosition) {
        if(isHandled(stack)) {
            this.renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, null);
        } else {
            prevRenderItem.renderItemOverlays(fr, stack, xPosition, yPosition);
        }
    }

    public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text) {
        if (isHandled(stack)) {
            if (stack.stackSize != 1 || text != null) {
                String s = text == null ? String.valueOf(stack.stackSize) : text;
                if (text == null && stack.stackSize < 1) {
                    s = EnumChatFormatting.RED + String.valueOf(stack.stackSize);
                }
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableBlend();
                fr.drawStringWithShadow(s, (float) (xPosition + 19 - 2 - fr.getStringWidth(s)), (float) (yPosition + 6 + 3), 16777215);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
            if (stack.getItem().showDurabilityBar(stack)) {
                double health = stack.getItem().getDurabilityForDisplay(stack);
                int j = (int) Math.round(13.0D - health * 13.0D);
                int i = (int) Math.round(255.0D - health * 255.0D);
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                this.drawRectangle(worldrenderer, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
                this.drawRectangle(worldrenderer, xPosition + 2, yPosition + 13, 12, 1, (255 - i) / 4, 64, 0, 255);
                this.drawRectangle(worldrenderer, xPosition + 2, yPosition + 13, j, 1, 255 - i, i, 0, 255);
                GlStateManager.enableAlpha();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        } else {
            prevRenderItem.renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, text);
        }
    }

    private void drawRectangle(WorldRenderer worldRenderer, int x, int y, int width, int height, int red, int blue, int green, int alpha) {
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos((double) (x ), (double) (y), 0.0D).color(red, blue, green, alpha).endVertex();
        worldRenderer.pos((double) (x), (double) (y + height), 0.0D).color(red, blue, green, alpha).endVertex();
        worldRenderer.pos((double) (x + width), (double) (y + height), 0.0D).color(red, blue, green, alpha).endVertex();
        worldRenderer.pos((double) (x + width), (double) (y), 0.0D).color(red, blue, green, alpha).endVertex();
        Tessellator.getInstance().draw();
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.getItemModelMesher().rebuildCache();
    }

    private static TextureManager getTextureManager(RenderItem renderItem) {
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        for(Field field : renderItem.getClass().getDeclaredFields()) {
            if(field.getType() == TextureManager.class) {
                field.setAccessible(true);
                try {
                    textureManager = (TextureManager) field.get(renderItem);
                } catch (Exception e) {
                    LogHelper.printStackTrace(e);
                }
                field.setAccessible(false);
                break;
            } else if(field.getType() == RenderItem.class) {
                field.setAccessible(true);
                try {
                    //Recursive, in case someone wrapped the RenderItem too
                    textureManager = getTextureManager((RenderItem) field.get(renderItem));
                } catch (Exception e) {
                    LogHelper.printStackTrace(e);
                }
                field.setAccessible(false);
                break;
            }
        }
        return textureManager;
    }

    private static ModelManager getModelManager() {
        for(Field field : Minecraft.class.getDeclaredFields()) {
            if(field.getType() == ModelManager.class) {
                field.setAccessible(true);
                try {
                    return (ModelManager) field.get(Minecraft.getMinecraft());
                } catch(Exception e) {
                    LogHelper.printStackTrace(e);
                }
                field.setAccessible(false);
            }
        }
        return ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "aL", "field_175617_aL", "modelManager");
    }

    private static void applyRenderItem() {
        for(Field field : Minecraft.class.getDeclaredFields()) {
            if(field.getType() == RenderItem.class) {
                field.setAccessible(true);
                try {
                    field.set(Minecraft.getMinecraft(), INSTANCE);
                } catch (Exception e) {
                    LogHelper.printStackTrace(e);
                }
                field.setAccessible(false);
                continue;
            }
            if (field.getType() == ItemRenderer.class) {
                field.setAccessible(true);
                try {
                    field.set(Minecraft.getMinecraft(), new ItemRenderer(Minecraft.getMinecraft()));
                } catch (Exception e) {
                    LogHelper.printStackTrace(e);
                }
                field.setAccessible(false);
            }
        }
    }

    private static void resetRenderManagerEntries() {
        Minecraft minecraft = Minecraft.getMinecraft();
        RenderManager renderManager = minecraft.getRenderManager();
        renderManager.entityRenderMap.remove(EntityItem.class);
        renderManager.entityRenderMap.put(EntityItem.class, new RenderEntityItem(renderManager, INSTANCE));
        renderManager.entityRenderMap.remove(EntityItemFrame.class);
        renderManager.entityRenderMap.put(EntityItemFrame.class, new RenderItemFrame(renderManager, INSTANCE));
        minecraft.entityRenderer = new EntityRenderer(minecraft, minecraft.getResourceManager());
    }
}
