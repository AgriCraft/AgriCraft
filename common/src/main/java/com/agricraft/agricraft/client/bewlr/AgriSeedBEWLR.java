package com.agricraft.agricraft.client.bewlr;

import com.agricraft.agricraft.common.util.PlatformClientUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class AgriSeedBEWLR extends BlockEntityWithoutLevelRenderer {

	public static final AgriSeedBEWLR INSTANCE = new AgriSeedBEWLR();
	private static final String DEFAULT_SEED = "agricraft:unknown";

	public AgriSeedBEWLR() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		String plant = stack.getTag() == null ? DEFAULT_SEED : stack.getTag().getString("seed");
		if (plant.isEmpty()) {
			plant = DEFAULT_SEED;
		}
		plant = plant.replace(":", ":seed/");

		BakedModel model = Minecraft.getInstance().getModelManager().bakedRegistry.get(new ResourceLocation(plant));
		if (model == null) {
			model = Minecraft.getInstance().getModelManager().getMissingModel();
		}

		PlatformClientUtils.renderItem(model, stack, itemDisplayContext, poseStack, buffer, packedLight, packedOverlay);
	}

	private static boolean isLeftHand(ItemDisplayContext itemDisplayContext) {
		return itemDisplayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || itemDisplayContext == ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
	}

}
