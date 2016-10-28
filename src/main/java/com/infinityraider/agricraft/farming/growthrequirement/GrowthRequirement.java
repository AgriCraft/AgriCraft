package com.infinityraider.agricraft.farming.growthrequirement;

import java.util.Collection;
import java.util.List;

import com.infinityraider.agricraft.api.requirement.ICondition;
import com.infinityraider.agricraft.api.requirement.IGrowthRequirement;
import com.infinityraider.agricraft.api.soil.IAgriSoil;

/**
 * Encodes all requirements a plant needs to mutate and grow Uses the Builder
 * class inside to construct instances.
 */
public class GrowthRequirement implements IGrowthRequirement {

    /**
     * Minimum allowed brightness, inclusive *
     */
    private final int minLight;

    /**
     * Maximum allowed brightness, exclusive *
     */
    private final int maxLight;

    private final List<IAgriSoil> soils;
    private final List<ICondition> conditions;

    GrowthRequirement(List<IAgriSoil> soils, List<ICondition> conditions, int minLight, int maxLight) {
        this.soils = soils;
        this.conditions = conditions;
        if (minLight < maxLight) {
            this.minLight = minLight;
            this.maxLight = maxLight;
        } else {
            this.minLight = maxLight;
            this.maxLight = minLight;
        }
    }

    @Override
    public Collection<IAgriSoil> getSoils() {
        return this.soils;
    }

    @Override
    public Collection<ICondition> getConditions() {
        return this.conditions;
    }

    @Override
    public int getMinLight() {
        return this.minLight;
    }

    @Override
    public int getMaxLight() {
        return this.maxLight;
    }

}
