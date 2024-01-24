package com.agricraft.agricraft.client.bewlr;

import com.agricraft.agricraft.api.AgriClientApi;
import com.agricraft.agricraft.common.item.AgriSeedItem;
import com.agricraft.agricraft.common.util.PlatformClient;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
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
		BakedModel seedModel = AgriClientApi.getSeedModel(plant);
		// render the item using the computed model
		PlatformClient.get().renderItem(seedModel, stack, itemDisplayContext, poseStack, buffer, packedLight, packedOverlay);
	}

}
