package com.agricraft.agricraft.client.bewlr;

import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.agricraft.agricraft.common.util.PlatformClientUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Renderer for the agricraft seed item
 */
public class AgriSeedBEWLR extends BlockEntityWithoutLevelRenderer {

	public static final AgriSeedBEWLR INSTANCE = new AgriSeedBEWLR();
	private static final String DEFAULT_SEED = "agricraft:unknown";

	public AgriSeedBEWLR() {
		super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
	}

	@Override
	public void renderByItem(ItemStack stack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		String plant = AgriSeedItem.getSpecies(stack);
		if (plant.isEmpty()) {
			plant = DEFAULT_SEED;
		}
		// compute the model of the seed from the plant id. the seed model path will look like <namespace>:seed/<id> so the file is /assets/<namespace>/models/seed/<id>.json
		plant = plant.replace(":", ":seed/");

		BakedModel model = Minecraft.getInstance().getModelManager().bakedRegistry.get(new ResourceLocation(plant));
		if (model == null) {
			// model not found, defaults to the missing model
			model = Minecraft.getInstance().getModelManager().getMissingModel();
		}

		// render the item using the computed model
		PlatformClientUtils.renderItem(model, stack, itemDisplayContext, poseStack, buffer, packedLight, packedOverlay);
	}

}
