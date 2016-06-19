package com.infinityraider.agricraft.compat.json;

import com.infinityraider.agricraft.compat.ModHelper;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import com.infinityraider.agricraft.farming.mutation.MutationHandler;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.utility.exception.DuplicateCropPlantException;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.infinityraider.agricraft.api.v3.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v3.mutation.IAgriMutation;

public class JsonHelper extends ModHelper {

	private static final List<JsonCropPlant> customCrops = new ArrayList<>();

	public JsonHelper() {
		super("JSON");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void textureStitch() {
		initPlantTextures();
	}

	@Override
	protected void serverStart() {
		initPlants();
		initMutations();
	}

	public static void initPlants() {
		AgriCore.getPlants().validate();
		AgriCore.getLogger("AgriCraft").info("Registering Custom Plants!");
		AgriCore.getPlants().validate();
		AgriCore.getPlants().getAll().forEach((p) -> {
			JsonCropPlant c = new JsonCropPlant(p);
			try {
				CropPlantHandler.registerPlant(c);
				customCrops.add(c);
			} catch (DuplicateCropPlantException e) {
				AgriCore.getLogger("AgriCraft").debug("Duplicate plant: " + p.getName() + "!");
			}
		});
		AgriCore.getLogger("AgriCraft").info("Custom crops registered!");
	}

	public static void initMutations() {
		AgriCore.getMutations().validate();
		AgriCore.getMutations().getAll().forEach((m) -> {
			IAgriPlant child = findPlant(m.getChild().getId());
			IAgriPlant p1 = findPlant(m.getParent1().getId());
			IAgriPlant p2 = findPlant(m.getParent2().getId());
			if (child != null && p1 != null && p2 != null) {
				MutationHandler.add(new Mutation(m.getChance(), null, child, p1, p2));
			}
		});
		//print registered mutations to the log
		AgriCore.getLogger("AgriCraft").info("Registered Mutations:");
		for (IAgriMutation mutation : MutationHandler.getMutations()) {
			AgriCore.getLogger("AgriCraft").info(" - {0}", mutation);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void initPlantTextures() {
		AgriCore.getLogger("AgriCraft").debug("Starting custom plant texture registration...");
		customCrops.forEach((p) -> {
			p.registerIcons();
		});
		AgriCore.getLogger("AgriCraft").debug("Registered custom plant textures!");
	}

	private static IAgriPlant findPlant(String id) {
		//AgriCore.getLogger("AgriCraft").debug("Looking for plant: " + id);
		for (IAgriPlant p : CropPlantHandler.getPlants()) {
			String other;
			if (p.getBlock() != null) {
				other = p.getBlock().getUnlocalizedName().replaceAll(".*:crop", "");
			} else if (p instanceof JsonCropPlant) {
				other = ((JsonCropPlant) p).plant.getId();
			} else {
				continue;
			}
			//AgriCore.getLogger("AgriCraft").debug("Saw plant: " + other);
			if (other.equalsIgnoreCase(id)) {
				//AgriCore.getLogger("AgriCraft").debug("Found Plant: " + other + "!");
				return p;
			}
		}
		//AgriCore.getLogger("AgriCraft").debug("Couldn't find plant: " + id + "!");
		return null;
	}

}
