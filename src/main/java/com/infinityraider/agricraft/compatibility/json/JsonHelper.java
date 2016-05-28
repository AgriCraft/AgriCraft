package com.infinityraider.agricraft.compatibility.json;

import com.infinityraider.agricraft.compatibility.ModHelper;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import com.infinityraider.agricraft.farming.mutation.MutationHandler;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.utility.exception.DuplicateCropPlantException;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;

public class JsonHelper extends ModHelper {

	private final List<JsonCropPlant> customCrops;

	public JsonHelper() {
		super("JSON");
		this.customCrops = new ArrayList<>();
	}

	@Override
	protected void postInit() {
		initCrops();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void textureStitch() {
		initPlantTextures();
	}

	@Override
	protected void serverStart() {
		initMutations();
	}

	public void initCrops() {
		AgriCore.getLogger("AgriCraft").info("Registering Custom Crops!");
		AgriCore.getPlants().validate();
		AgriCore.getPlants().getAll().forEach((p) -> {
			JsonCropPlant c = new JsonCropPlant(p);
			try {
				CropPlantHandler.registerPlant(c);
				this.customCrops.add(c);
			} catch (DuplicateCropPlantException e) {
				AgriCore.getLogger("AgriCraft").debug("Duplicate plant: " + p.getName() + "!");
			}
		});
		AgriCore.getLogger("AgriCraft").info("Custom crops registered!");
	}

	public void initMutations() {
		AgriCore.getMutations().getAll().forEach((m) -> {
			IAgriCraftPlant child = findPlant(m.getChild().getId());
			IAgriCraftPlant p1 = findPlant(m.getParent1().getId());
			IAgriCraftPlant p2 = findPlant(m.getParent2().getId());
			if (child != null && p1 != null && p2 != null) {
				MutationHandler.add(new Mutation(child.getSeed(), p1.getSeed(), p2.getSeed(), m.getChance()));
			}
		});
	}

	@SideOnly(Side.CLIENT)
	public void initPlantTextures() {
		AgriCore.getLogger("AgriCraft").debug("Starting custom plant texture registration...");
		this.customCrops.forEach((p) -> {
			p.registerIcons();
		});
		AgriCore.getLogger("AgriCraft").debug("Registered custom plant textures!");
	}

	private static final IAgriCraftPlant findPlant(String id) {
		AgriCore.getLogger("AgriCraft").debug("Looking for plant: " + id);
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
				AgriCore.getLogger("AgriCraft").debug("Found Plant: " + other + "!");
				return p;
			}
		}
		AgriCore.getLogger("AgriCraft").debug("Couldn't find plant: " + id + "!");
		return null;
	}

}
