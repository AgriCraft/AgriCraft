package com.agricraft.agricraft.common.util.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PlatformClientUtilsImpl {

	public static void renderItem(BakedModel model, ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		// https://gist.github.com/XFactHD/11ccae6a54da62909caf6d555cd4d8b9
		ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

		poseStack.popPose();
		poseStack.pushPose();

		model.getTransforms().getTransform(itemDisplayContext).apply(isLeftHand(itemDisplayContext), poseStack);
		poseStack.translate(-.5, -.5, -.5);

		boolean glint = stack.hasFoil();
		RenderType type = getEntityRenderType(RenderType.cutoutMipped(), true);
		VertexConsumer consumer = ItemRenderer.getFoilBuffer(buffer, type, true, glint);
		renderer.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, consumer);
	}

	private static RenderType getEntityRenderType(RenderType chunkRenderType, boolean cull) {
		if (chunkRenderType != RenderType.translucent()) {
			return Sheets.cutoutBlockSheet();
		}
		return cull || !Minecraft.useShaderTransparency() ? Sheets.translucentCullBlockSheet() : Sheets.translucentItemSheet();
	}

	private static boolean isLeftHand(ItemDisplayContext itemDisplayContext) {
		return itemDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || itemDisplayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
	}

}
