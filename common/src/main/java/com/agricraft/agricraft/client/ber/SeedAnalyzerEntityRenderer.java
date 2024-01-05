package com.agricraft.agricraft.client.ber;

import com.agricraft.agricraft.common.block.entity.SeedAnalyzerBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Renderer for the agricraft crop block
 */
public class SeedAnalyzerEntityRenderer implements BlockEntityRenderer<SeedAnalyzerBlockEntity> {

	public SeedAnalyzerEntityRenderer(BlockEntityRendererProvider.Context context) {

	}

	@Override
	public void render(SeedAnalyzerBlockEntity analyzer, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		if (analyzer.hasSeed()) {
			ItemStack seed = analyzer.getSeed();
			poseStack.pushPose();
			poseStack.translate(0.5, 0.5, 0.5);
			float angle = (float) ((720F * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL) * Math.PI / 180F);
			poseStack.mulPose(new Quaternionf(new AxisAngle4f(angle, new Vector3f(0, 1, 0))));  // Vector3f.YP, this.calculateAngle(), true
			Minecraft.getInstance().getItemRenderer().renderStatic(seed, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, buffer, null, 0);
			poseStack.popPose();
		}
	}

}
