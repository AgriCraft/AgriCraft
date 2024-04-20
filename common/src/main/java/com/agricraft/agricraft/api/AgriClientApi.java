package com.agricraft.agricraft.api;

import com.agricraft.agricraft.api.tools.journal.JournalPageDrawer;
import com.agricraft.agricraft.api.tools.journal.JournalPageDrawers;
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
	private static final String UNKNOWN_SEED = "agricraft:unknown";
	private static final String UNKNOWN_PLANT = "agricraft:crop/unknown";


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

	public static BakedModel getPlantModel(ResourceLocation plantId, int stage) {
		return getPlantModel(plantId.toString(), stage);
	}

	public static BakedModel getPlantModel(String plantId, int stage) {
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
				return Minecraft.getInstance().getModelManager().bakedRegistry.get(new ResourceLocation(UNKNOWN_PLANT));
			}
			return model;
		}
	}

	public static BakedModel getWeedModel(String weedId, int stage) {
		if (weedId.isEmpty()) {
			// somehow there is no plant, display nothing
			return Minecraft.getInstance().getModelManager().bakedRegistry.get(AIR_MODEL);
		} else {
			// compute the block model from the plant id and growth stage
			// will look like <namespace>:weed/<id>_stage<growth_stage> so the file is assets/<namespace>/models/weed/<id>_stage<growth_stage>.json
			String plant = weedId.replace(":", ":weed/") + "_stage" + stage;
			BakedModel model = Minecraft.getInstance().getModelManager().bakedRegistry.get(new ResourceLocation(plant));
			if (model == null) {
				// model not found, default to the unknown crop model that should always be present
				return Minecraft.getInstance().getModelManager().bakedRegistry.get(new ResourceLocation(UNKNOWN_PLANT));
			}
			return model;
		}
	}

	public static BakedModel getSeedModel(String plantId) {
		if (plantId.isEmpty()) {
			plantId = UNKNOWN_SEED;
		}
		// compute the model of the seed from the plant id. the seed model path will look like <namespace>:seed/<id> so the file is /assets/<namespace>/models/seed/<id>.json
		plantId = plantId.replace(":", ":seed/");

		BakedModel model = Minecraft.getInstance().getModelManager().bakedRegistry.get(new ResourceLocation(plantId));
		if (model == null) {
			// model not found, defaults to the missing model
			model = Minecraft.getInstance().getModelManager().getMissingModel();
		}
		return model;
	}

	public static void registerPageDrawer(ResourceLocation id, JournalPageDrawer<?> pageDrawer) {
		JournalPageDrawers.registerPageDrawer(id, pageDrawer);
	}

}
