package com.infinityraider.agricraft.api.v1.plant;

import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;

public interface IAgriPlantRegistry extends IAgriRegistry<IAgriPlant> {
    /**
     * @return AgriCraft's placeholder IAgriPlant implementation representing the absence of a plant
     */
    IAgriPlant getNoPlant();
}
