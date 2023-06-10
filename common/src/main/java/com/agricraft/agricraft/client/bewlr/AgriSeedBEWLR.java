package com.agricraft.agricraft.client.bewlr;

import com.agricraft.agricraft.common.util.PlatformClientUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class AgriSeedBEWLR extends BlockEntityWithoutLevelRenderer {

	public static final AgriSeedBEWLR INSTANCE = new AgriSeedBEWLR();
	private static final String DEFAULT_MODEL = "minecraft:wheat_seeds";

	public AgriSeedBEWLR() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

		String plant = stack.getTag() == null ? DEFAULT_MODEL : stack.getTag().getString("plant");
		if (plant.isEmpty()) {
			plant = DEFAULT_MODEL;
		}

		BakedModel model = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(new ResourceLocation(plant), "inventory"));
		PlatformClientUtils.renderItem(model, stack, itemDisplayContext, poseStack, buffer, packedLight, packedOverlay);
	}

	private static boolean isLeftHand(ItemDisplayContext itemDisplayContext)
	{
		return itemDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || itemDisplayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
	}

//	private static class IconRenderType extends RenderType {
//		// Need to have a constructor...
//		public IconRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean crumbling, boolean sorted, Runnable runnablePre, Runnable runnablePost) {
//			super(name, format, mode, bufferSize, crumbling, sorted, runnablePre, runnablePost);
//		}
//
//		private static final RenderType INSTANCE = create("agri_journal_icons",
//				DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, false, false,
//				RenderType.CompositeState.builder()
//						.setShaderState(RenderStateShard.POSITION_COLOR_TEX_SHADER)
//						.setLightmapState(RenderStateShard.NO_LIGHTMAP)
//						.createCompositeState(false));
//	}

}
