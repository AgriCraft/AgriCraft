package com.agricraft.agricraft.common.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public abstract class PlatformClient {
	private static PlatformClient platform = null;

	public static void setup(PlatformClient platform) {
		if (PlatformClient.platform == null) {
			PlatformClient.platform = platform;
		}
	}

	public static PlatformClient get() {
		return PlatformClient.platform;
	}

	public abstract void renderItem(BakedModel model, ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay);

}
