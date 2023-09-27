package com.agricraft.agricraft.client.ber;

import com.agricraft.agricraft.client.ClientUtil;
import com.agricraft.agricraft.common.block.CropBlock;
import com.agricraft.agricraft.common.block.entity.CropBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

/**
 * Renderer for the agricraft crop block
 */
public class CropBlockEntityRenderer implements BlockEntityRenderer<CropBlockEntity> {

	public CropBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

	}

	@Override
	public void render(CropBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		if (blockEntity.hasCropSticks()) {
			String modelId = ClientUtil.getModelForSticks(blockEntity.getBlockState().getValue(CropBlock.STICK_VARIANT));
			if (blockEntity.isCrossCropSticks()) {
				modelId = modelId.replace("crop", "cross_crop");
			}
			BakedModel model = Minecraft.getInstance().getModelManager().bakedRegistry.get(new ResourceLocation(modelId));
			// render the stick model
			Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(),
					buffer.getBuffer(RenderType.cutoutMipped()),
					blockEntity.getBlockState(), model, 1, 1, 1, packedLight, packedOverlay);
		}
		if (!blockEntity.hasPlant()) {
			return;
		}
		int stage = blockEntity.getGrowthStage();
		String plantId = blockEntity.getPlantId();
		if (!plantId.isEmpty()) {
			// compute the block model from the plant id and growth stage
			// will look like <namespace>:crop/<id>_stage<growth_stage> so the file is assets/<namespace>/models/crop/<id>_stage<growth_stage>.json
			String plant = plantId.replace(":", ":crop/") + "_stage" + stage;
			BakedModel model = Minecraft.getInstance().getModelManager().bakedRegistry.get(new ResourceLocation(plant));
			if (model == null) {
				// model not found, default to the unknown crop model that should always be present
				model = Minecraft.getInstance().getModelManager().bakedRegistry.get(new ResourceLocation("agricraft:crop/unknown"));
			}
			// render the computed plant model
			Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(),
					buffer.getBuffer(RenderType.cutoutMipped()),
					blockEntity.getBlockState(), model, 1, 1, 1, packedLight, packedOverlay);
		}

	}

}
