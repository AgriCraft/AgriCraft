package com.agricraft.agricraft.common.util.forge;

import com.agricraft.agricraft.common.util.PlatformClient;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.RenderTypeHelper;

/**
 * NeoForge implementation of {@link PlatformClient}
 */
public class NeoForgePlatformClient extends PlatformClient {

	@Override
	public void renderItem(BakedModel model, ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		// https://gist.github.com/XFactHD/11ccae6a54da62909caf6d555cd4d8b9
		ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

		poseStack.popPose();
		poseStack.pushPose();

		model = model.applyTransform(itemDisplayContext, poseStack, isLeftHand(itemDisplayContext));
		poseStack.translate(-.5, -.5, -.5);

		boolean glint = stack.hasFoil();
		for (RenderType type : model.getRenderTypes(stack, true)) {
			type = RenderTypeHelper.getEntityRenderType(type, true);
			VertexConsumer consumer = ItemRenderer.getFoilBuffer(buffer, type, true, glint);
			renderer.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, consumer);
		}
	}

	private static boolean isLeftHand(ItemDisplayContext itemDisplayContext) {
		return itemDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || itemDisplayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
	}

}
