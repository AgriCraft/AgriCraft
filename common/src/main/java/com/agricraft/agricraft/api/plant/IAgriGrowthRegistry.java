package com.agricraft.agricraft.api.plant;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.crop.IAgriGrowthStage;
import com.agricraft.agricraft.api.util.IAgriRegistry;

public interface IAgriGrowthRegistry extends IAgriRegistry<IAgriGrowthStage> {

	/**
	 * @return the AgriCraft IAgriGrowthRegistry instance
	 */
	static IAgriGrowthRegistry getInstance() {
		return AgriApi.getGrowthStageRegistry();
	}

	/**
	 * @return AgriCraft's placeholder IAgriGrowthStage implementation representing the absence of a growth stage
	 */
	IAgriGrowthStage getNoGrowth();

}
