package com.infinityraider.agricraft.impl.v1.plant;

import com.infinityraider.agricraft.api.v1.plant.IAgriGrowthStage;
import com.infinityraider.agricraft.impl.v1.AgriRegistry;

public class AgriGrowthRegistry extends AgriRegistry<IAgriGrowthStage> {
    private static final AgriGrowthRegistry INSTANCE = new AgriGrowthRegistry();

    public static AgriGrowthRegistry getInstance() {
        return INSTANCE;
    }

    public AgriGrowthRegistry() {
        super("growth", IAgriGrowthStage.class);
    }
}
