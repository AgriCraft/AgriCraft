package com.infinityraider.agricraft.client.renderers.renderinghacks;

import com.infinityraider.agricraft.utility.LogHelper;
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
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ReportedException;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

@SideOnly(Side.CLIENT)
@SuppressWarnings("deprecation")
public final class RenderItemWrapped extends RenderItem {

	private static RenderItemWrapped INSTANCE;
	private static IRenderingRegistry renderingRegistry;

	private static RenderItem prevRenderItem;
	private final TextureManager textureManager;

	public static void init() {
		prevRenderItem = Minecraft.getMinecraft().getRenderItem();
		TextureManager textureManager = getTextureManager(prevRenderItem);
		ModelManager modelManager = getModelManager();
		INSTANCE = new RenderItemWrapped(textureManager, modelManager);
		applyRenderItem();
		resetRenderManagerEntries();
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(INSTANCE);
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(Minecraft.getMinecraft().entityRenderer);
		renderingRegistry = BlockRendererDispatcherWrapped.getInstance();
	}

	public static RenderItemWrapped getInstance() {
		return INSTANCE;
	}

	private RenderItemWrapped(TextureManager textureManager, ModelManager modelManager) {
		super(textureManager, modelManager);
		this.textureManager = textureManager;
	}

	private void renderItemOverride(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
		GlStateManager.disableLighting();
		renderingRegistry.getItemRenderer(stack.getItem()).renderItem(stack, transformType);
		GlStateManager.enableLighting();
	}

	private boolean isHandled(ItemStack stack) {
		return stack != null
				&& stack.getItem() != null
				&& renderingRegistry.hasRenderingHandler(stack.getItem());
	}

	/*
	 * Overrides from previous RenderItem instance
	 */
	@Override
	public void func_175039_a(boolean b) {
		prevRenderItem.func_175039_a(b);
	}

	@Override
	public ItemModelMesher getItemModelMesher() {
		return prevRenderItem.getItemModelMesher();
	}

	@Override
	protected void registerItem(Item itm, int subType, String identifier) {
		prevRenderItem.getItemModelMesher().register(itm, subType, new ModelResourceLocation(identifier, "inventory"));
	}

	@Override
	protected void registerBlock(Block blk, int subType, String identifier) {
		this.registerItem(Item.getItemFromBlock(blk), subType, identifier);
	}

	@Override
	public void renderItem(ItemStack stack, IBakedModel model) {
		if (isHandled(stack)) {
			this.renderItemOverride(stack, ItemCameraTransforms.TransformType.GROUND);
		} else {
			prevRenderItem.renderItem(stack, model);
		}
	}

	@Override
	public boolean shouldRenderItemIn3D(ItemStack stack) {
		if (this.isHandled(stack)) {
			return renderingRegistry.getItemRenderer(stack.getItem()).shouldRender3D(stack);
		} else {
			return prevRenderItem.shouldRenderItemIn3D(stack);
		}
	}

	@Override
	public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
		if (isHandled(stack)) {
			renderItemOverride(stack, transformType);
		} else {
			prevRenderItem.renderItem(stack, transformType);
		}
	}

	@Override
	public void renderItemModelForEntity(ItemStack stack, EntityLivingBase entityToRenderFor, ItemCameraTransforms.TransformType cameraTransformType) {
		if (stack != null && entityToRenderFor != null) {
			if (isHandled(stack)) {
				renderItemOverride(stack, cameraTransformType);
			} else {
				prevRenderItem.renderItemModelForEntity(stack, entityToRenderFor, cameraTransformType);
			}
		}
	}

	@Override
	public void renderItemIntoGUI(ItemStack stack, int x, int y) {
		if (isHandled(stack)) {
			GlStateManager.pushMatrix();
			this.textureManager.bindTexture(TextureMap.locationBlocksTexture);
			this.textureManager.getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableAlpha();
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 771);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.setupGuiTransform(x, y, true);

			renderItemOverride(stack, ItemCameraTransforms.TransformType.GUI);

			GlStateManager.disableAlpha();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableLighting();
			GlStateManager.popMatrix();
			this.textureManager.bindTexture(TextureMap.locationBlocksTexture);
			this.textureManager.getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
		} else {
			prevRenderItem.renderItemIntoGUI(stack, x, y);
		}
	}

	private void setupGuiTransform(int xPosition, int yPosition, boolean isGui3d) {
		GlStateManager.translate((float) xPosition, (float) yPosition, 100.0F + this.zLevel);
		GlStateManager.translate(8.0F, 8.0F, 0.0F);
		GlStateManager.scale(1.0F, 1.0F, -1.0F);
		GlStateManager.scale(0.5F, 0.5F, 0.5F);
		if (isGui3d) {
			GlStateManager.scale(40.0F, 40.0F, 40.0F);
			GlStateManager.rotate(210.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.enableLighting();
		} else {
			GlStateManager.scale(64.0F, 64.0F, 64.0F);
			GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.disableLighting();
		}
	}

	@Override
	public void renderItemAndEffectIntoGUI(final ItemStack stack, int xPosition, int yPosition) {
		if (stack != null && stack.getItem() != null) {
			if (isHandled(stack)) {
				this.zLevel -= 50.0F;
				try {
					this.renderItemIntoGUI(stack, xPosition, yPosition);
				} catch (Throwable throwable) {
					CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering item");
					CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being rendered");
					crashreportcategory.addCrashSectionCallable("Item Type", () -> String.valueOf(stack.getItem()));
					crashreportcategory.addCrashSectionCallable("Item Aux", () -> String.valueOf(stack.getMetadata()));
					crashreportcategory.addCrashSectionCallable("Item NBT", () -> String.valueOf(stack.getTagCompound()));
					crashreportcategory.addCrashSectionCallable("Item Foil", () -> String.valueOf(stack.hasEffect()));
					throw new ReportedException(crashreport);
				}
				this.zLevel += 50.0F;
			} else {
				prevRenderItem.renderItemAndEffectIntoGUI(stack, xPosition, yPosition);
			}
		}
	}

	@Override
	public void renderItemOverlays(FontRenderer fr, ItemStack stack, int xPosition, int yPosition) {
		prevRenderItem.renderItemOverlays(fr, stack, xPosition, yPosition);
	}

	@Override
	public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text) {
		prevRenderItem.renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, text);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		prevRenderItem.onResourceManagerReload(resourceManager);
	}

	/**
	 * Methods requiring reflection to do hacky stuff
	 */
	private static TextureManager getTextureManager(RenderItem renderItem) {
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		for (Field field : renderItem.getClass().getDeclaredFields()) {
			if (field.getType() == TextureManager.class) {
				field.setAccessible(true);
				try {
					textureManager = (TextureManager) field.get(renderItem);
				} catch (Exception e) {
					LogHelper.printStackTrace(e);
				}
				field.setAccessible(false);
				break;
			} else if (field.getType() == RenderItem.class) {
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
		for (Field field : Minecraft.class.getDeclaredFields()) {
			if (field.getType() == ModelManager.class) {
				field.setAccessible(true);
				try {
					return (ModelManager) field.get(Minecraft.getMinecraft());
				} catch (Exception e) {
					LogHelper.printStackTrace(e);
				}
				field.setAccessible(false);
			}
		}
		return ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "aL", "field_175617_aL", "modelManager");
	}

	@SuppressWarnings("unchecked")
	private static void applyRenderItem() {
		for (Field field : Minecraft.class.getDeclaredFields()) {
			if (field.getType() == RenderItem.class) {
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
		IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
		for (Field field : manager.getClass().getDeclaredFields()) {
			if (field.getType() == List.class) {
				field.setAccessible(true);
				try {
					List<IResourceManagerReloadListener> list = (List<IResourceManagerReloadListener>) field.get(manager);
					Iterator<IResourceManagerReloadListener> it = list.iterator();
					while (it.hasNext()) {
						IResourceManagerReloadListener listener = it.next();
						if (listener instanceof RenderItem) {
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
