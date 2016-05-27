package com.infinityraider.agricraft.compatibility.json;

import com.infinityraider.agricraft.api.v1.ICropPlant;
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

public class JsonHelper extends ModHelper {

	private final List<JsonCropPlant> customCrops;
	private final List<JsonItemSeed> customSeeds;

	public JsonHelper() {
		super("JSON");
		this.customCrops = new ArrayList<>();
		this.customSeeds = new ArrayList<>();
	}

	@Override
	protected void postInit() {
		initCrops();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void textureStitch() {
		initSeedTextures();
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
				this.customSeeds.add(c.seed);
			} catch (DuplicateCropPlantException e) {
				AgriCore.getLogger("AgriCraft").debug("Duplicate plant: " + p.getName() + "!");
			}
		});
		AgriCore.getLogger("AgriCraft").info("Custom crops registered!");
	}

	public void initMutations() {
		AgriCore.getMutations().getAll().forEach((m) -> {
			ICropPlant child = findPlant(m.getChild().getId());
			ICropPlant p1 = findPlant(m.getParent1().getId());
			ICropPlant p2 = findPlant(m.getParent2().getId());
			if (child != null && p1 != null && p2 != null) {
				MutationHandler.add(new Mutation(child.getSeed(), p1.getSeed(), p2.getSeed(), m.getChance()));
			}
		});
	}

	@SideOnly(Side.CLIENT)
	public void initSeedTextures() {
		AgriCore.getLogger("AgriCraft").debug("Starting custom seed renderer registration...");
		for (JsonItemSeed seed : customSeeds) {
			try {
				seed.registerItemRenderer();
				AgriCore.getLogger("AgriCraft").info("Registered Renderer for: " + seed.getRegistryName());
			} catch (Exception e) {
				AgriCore.getLogger("AgriCraft").trace(e);
			}
		}
		AgriCore.getLogger("AgriCraft").debug("Registered custom seed renderers!");
	}

	@SideOnly(Side.CLIENT)
	public void initPlantTextures() {
		AgriCore.getLogger("AgriCraft").debug("Starting custom plant texture registration...");
		this.customCrops.forEach((p) -> {
			p.registerIcons();
		});
		AgriCore.getLogger("AgriCraft").debug("Registered custom plant textures!");
	}

	private static final ICropPlant findPlant(String id) {
		AgriCore.getLogger("AgriCraft").debug("Looking for plant: " + id);
		for (ICropPlant p : CropPlantHandler.getPlants()) {
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
