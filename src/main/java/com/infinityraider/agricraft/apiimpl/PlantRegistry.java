/*
 */
package com.infinityraider.agricraft.apiimpl;

import java.util.List;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.compat.jei.AgriCraftJEIPlugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.infinityraider.agricraft.api.plant.IAgriPlantRegistry;

/**
 *
 * @author RlonRyan
 */
public class PlantRegistry implements IAgriPlantRegistry {
	
	private final Map<String, IAgriPlant> plants;

	public PlantRegistry() {
		this.plants = new HashMap<>();
	}
	
	public static IAgriPlantRegistry getInstance() {
		return AgriApiImpl.getInstance().getPlantRegistry();
	}

	@Override
	public boolean isPlant(IAgriPlant plant) {
		return this.plants.containsKey(plant.getId());
	}

	@Override
	public IAgriPlant getPlant(String id) {
		return this.plants.get(id);
	}

	@Override
	public boolean addPlant(IAgriPlant plant) {
		if (!this.plants.containsKey(plant.getId())) {
			this.plants.put(plant.getId(), plant);
			AgriCraftJEIPlugin.registerRecipe(plant);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean removePlant(IAgriPlant plant) {
		return this.plants.remove(plant.getId()) != null;
	}

	@Override
	public List<IAgriPlant> getPlants() {
		return new ArrayList<>(this.plants.values());
	}

	@Override
	public List<String> getPlantIds() {
		return new ArrayList<>(this.plants.keySet());
	}
	
}
