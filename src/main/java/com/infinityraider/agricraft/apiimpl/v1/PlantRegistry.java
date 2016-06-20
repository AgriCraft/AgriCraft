/*
 */
package com.infinityraider.agricraft.apiimpl.v1;

import com.infinityraider.agricraft.api.v1.plant.IPlantRegistry;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.utility.exception.DuplicateCropPlantException;
import java.util.List;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;

/**
 *
 * @author RlonRyan
 */
public class PlantRegistry implements IPlantRegistry {
	
	public static IPlantRegistry getInstance() {
		return APIimplv1.getInstance().getPlantRegistry();
	}

	@Override
	public boolean isPlant(IAgriPlant plant) {
		return CropPlantHandler.getPlantIds().contains(plant.getId());
	}

	@Override
	public boolean addPlant(IAgriPlant plant) {
		try {
			CropPlantHandler.registerPlant(plant);
			return true;
		} catch (DuplicateCropPlantException e) {
			return false;
		}
	}

	@Override
	public List<IAgriPlant> getPlants() {
		return CropPlantHandler.getPlants();
	}

	@Override
	public List<String> getPlantIds() {
		return CropPlantHandler.getPlantIds();
	}
	
}
