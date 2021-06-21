package com.infinityraider.agricraft.api.v1.plant;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.util.IAgriRegistry;

public interface IAgriPlantRegistry extends IAgriRegistry<IAgriPlant> {
    /**
     * @return the AgriCraft IAgriPlantRegistry instance
     */
    @SuppressWarnings("unused")
    static IAgriPlantRegistry getInstance() {
        return AgriApi.getPlantRegistry();
    }

    /**
     * @return AgriCraft's placeholder IAgriPlant implementation representing the absence of a plant
     */
    IAgriPlant getNoPlant();
}
