package com.infinityraider.agricraft.api.v1.plant;

import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Common interface between all "things" that can grow on crop sticks
 */
public interface IAgriGrowable {
    /**
     * Determines the initial Growth Stage of a plant or weed when first planted
     *
     * @return the IAgriGrowthStage for the initial growth stage
     */
    @Nonnull
    IAgriGrowthStage getInitialGrowthStage();

    /**
     * Gets all the possible growth stages that a plant or weed can have.
     *
     * For AgriCraft specifically, the conventional number of growth stages is 8.
     *
     * @return a set containing all the possible growth stages of the plant.
     */
    @Nonnull
    Collection<IAgriGrowthStage> getGrowthStages();
}
