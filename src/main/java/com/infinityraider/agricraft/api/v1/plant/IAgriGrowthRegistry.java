package com.infinityraider.agricraft.api.v1.plant;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;

public interface IAgriGrowthRegistry extends IAgriRegistry<IAgriGrowthStage> {
    /**
     * @return the AgriCraft IAgriGrowthRegistry instance
     */
    @SuppressWarnings("unused")
    static IAgriGrowthRegistry getInstance() {
        return AgriApi.getGrowthStageRegistry();
    }

    /**
     * @return AgriCraft's placeholder IAgriGrowthStage implementation representing the absence of a growth stage
     */
    IAgriGrowthStage getNoGrowth();
}
