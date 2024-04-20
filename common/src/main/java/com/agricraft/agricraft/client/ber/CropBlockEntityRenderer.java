package com.agricraft.agricraft.client.ber;

import com.agricraft.agricraft.api.AgriClientApi;
import com.agricraft.agricraft.api.crop.AgriGrowthStage;
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
import net.minecraft.client.resources.model.ModelResourceLocation;
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
		if (blockEntity.hasPlant()) {
			AgriGrowthStage stage = blockEntity.getGrowthStage();
			String plantId = blockEntity.getPlantId();
			BakedModel plantModel = AgriClientApi.getPlantModel(plantId, stage.index());
			// render the computed plant model
			Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(),
					buffer.getBuffer(RenderType.cutoutMipped()),
					blockEntity.getBlockState(), plantModel, 1, 1, 1, packedLight, packedOverlay);
		}
		if (blockEntity.hasWeeds()) {
			AgriGrowthStage weedStage = blockEntity.getWeedGrowthStage();
			String weedId = blockEntity.getWeedId();
			BakedModel weedModel = AgriClientApi.getWeedModel(weedId, weedStage.index());
			// render the computed plant model
			Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(),
					buffer.getBuffer(RenderType.cutoutMipped()),
					blockEntity.getBlockState(), weedModel, 1, 1, 1, packedLight, packedOverlay);
		}

	}

}
