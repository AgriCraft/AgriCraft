package com.agricraft.agricraft.client.ber;

import com.agricraft.agricraft.common.block.CropBlock;
import com.agricraft.agricraft.common.block.entity.CropBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public class CropBlockEntityRenderer implements BlockEntityRenderer<CropBlockEntity> {

//	public static final ModelResourceLocation AIR_MODEL = new ModelResourceLocation("minecraft", "air", "");
	public CropBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

	}

	@Override
	public void render(CropBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

		int stage = blockEntity.getBlockState().getValue(CropBlock.AGE);
		String seed = blockEntity.getSeed();
		BakedModel model;
		if (seed.isEmpty()) {
			model = Minecraft.getInstance().getModelManager().bakedRegistry.get(new ModelResourceLocation("minecraft", "air", ""));
		} else {
			String plant = seed.replace(":", ":crop/") + "_stage" + stage;

			model = Minecraft.getInstance().getModelManager().bakedRegistry.get(new ResourceLocation(plant));
			if (model == null) {
				model = Minecraft.getInstance().getModelManager().bakedRegistry.get(new ResourceLocation("agricraft:crop/unknown"));
//				model = Minecraft.getInstance().getModelManager().bakedRegistry.get(new ModelResourceLocation("minecraft", "air", ""));
			}
		}

		Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(),
				buffer.getBuffer(RenderType.cutoutMipped()),
				blockEntity.getBlockState(), model, 1, 1, 1, packedLight, packedOverlay);
	}

}
