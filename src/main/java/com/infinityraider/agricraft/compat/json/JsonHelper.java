package com.infinityraider.agricraft.compat.json;

import com.infinityraider.agricraft.compat.ModHelper;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import com.infinityraider.agricraft.farming.mutation.MutationHandler;
import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriPlant;
import com.infinityraider.agricraft.utility.exception.DuplicateCropPlantException;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;

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
	@SideOnly(Side.SERVER)
	protected void serverStart() {
		initCrops();
		initMutations();
	}

	public static void initCrops() {
		AgriCore.getPlants().validate();
		AgriCore.getLogger("AgriCraft").info("Registering Custom Crops!");
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
			IAgriCraftPlant child = findPlant(m.getChild().getId());
			IAgriCraftPlant p1 = findPlant(m.getParent1().getId());
			IAgriCraftPlant p2 = findPlant(m.getParent2().getId());
			if (child != null && p1 != null && p2 != null) {
				MutationHandler.add(new Mutation(m.getChance(), null, child, p1, p2));
			}
		});
		//print registered mutations to the log
		AgriCore.getLogger("AgriCraft").info("Registered Mutations:");
		for (Mutation mutation : MutationHandler.getMutations()) {
			StringBuilder sb = new StringBuilder(" - ");
			for (int i = 0; i < mutation.getParents().length; i++) {
				sb.append(mutation.getParents()[i].getPlantName());
				if (i < mutation.getParents().length - 1) {
					sb.append(" + ");
				}
			}
			sb.append(" = ").append(mutation.getChild().getPlantName());
			AgriCore.getLogger("AgriCraft").info(sb.toString());
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

	private static IAgriCraftPlant findPlant(String id) {
		//AgriCore.getLogger("AgriCraft").debug("Looking for plant: " + id);
		for (IAgriCraftPlant p : CropPlantHandler.getPlants()) {
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
