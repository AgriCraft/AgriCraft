package com.infinityraider.agricraft.api.v1.plant;

import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Comparator;

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

    /**
     * @return the final growth stage for this plant
     */
    default IAgriGrowthStage getFinalStage() {
        return this.getGrowthStages().stream().filter(IAgriGrowthStage::isFinal).findAny()
                // This should not ever be reached
                .orElseThrow(() -> new IllegalStateException("Plant without final growth stage"));
    }

    /**
     * Fetches the height of the plant at the given growth stage in 1/16ths of a block.
     * Note that it is possible to return heights taller than 16
     *
     * For instance, returning 8 corresponds to a plant half a block high,
     * while returning 16 corresponds to a full block high,
     * and returning 32 corresponds to a plant which is two blocks high.     *
     *
     * @param stage the growth stage
     * @return height of the plant in 1/16ths of a block
     */
    double getPlantHeight(IAgriGrowthStage stage);
}
