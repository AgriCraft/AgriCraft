package com.agricraft.agricraft.api.plant;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.util.IAgriRegistry;

public interface IAgriPlantRegistry extends IAgriRegistry<IAgriPlant> {

	/**
	 * @return the AgriCraft IAgriPlantRegistry instance
	 */
	static IAgriPlantRegistry getInstance() {
		return AgriApi.getPlantRegistry();
	}

	/**
	 * @return AgriCraft's placeholder IAgriPlant implementation representing the absence of a plant
	 */
	IAgriPlant getNoPlant();

}
