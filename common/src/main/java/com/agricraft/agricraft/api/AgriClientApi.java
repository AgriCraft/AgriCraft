package com.agricraft.agricraft.api;

import com.agricraft.agricraft.api.tools.magnifying.MagnifyingInspector;
import com.agricraft.agricraft.client.gui.MagnifyingGlassOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

/**
 * The AgriCraft Client API v2
 */
public final class AgriClientApi {

	private static final ModelResourceLocation AIR_MODEL = new ModelResourceLocation("minecraft", "air", "");


	/**
	 * Add a magnifying inspector
	 *
	 * @param inspector the inspector
	 */
	public static void addMagnifyingInspector(MagnifyingInspector inspector) {
		MagnifyingGlassOverlay.addInspector(inspector);
	}

	/**
	 * Add a predicate to allow the overlay to render
	 *
	 * @param predicate the predicate
	 */
	public static void addMagnifyingAllowingPredicate(Predicate<Player> predicate) {
		MagnifyingGlassOverlay.addAllowingPredicate(predicate);
	}

	public static BakedModel getModel(String plantId, int stage) {
		if (plantId.isEmpty()) {
			// somehow there is no plant, display nothing
			return Minecraft.getInstance().getModelManager().bakedRegistry.get(AIR_MODEL);
		} else {
			// compute the block model from the plant id and growth stage
			// will look like <namespace>:crop/<id>_stage<growth_stage> so the file is assets/<namespace>/models/crop/<id>_stage<growth_stage>.json
			String plant = plantId.replace(":", ":crop/") + "_stage" + stage;
			BakedModel model = Minecraft.getInstance().getModelManager().bakedRegistry.get(new ResourceLocation(plant));
			if (model == null) {
				// model not found, default to the unknown crop model that should always be present
				return Minecraft.getInstance().getModelManager().bakedRegistry.get(new ResourceLocation("agricraft:crop/unknown"));
			}
			return model;
		}
	}

}
