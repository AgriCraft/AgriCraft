package com.infinityraider.agricraft.api.v1.crop;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.util.IAgriRegisterable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

/**
 * Interface governing the growth schemes of plants and weeds.
 * AgriCraft's default scheme follows an incremental logic, returning each time the next growth stage in a pre-defined list.
 * Such a list of default AgriCraft IAgriGrowthStage implementations can be obtained with AgriApi.getDefaultGrowthStages().
 *
 * More complex behaviour requires a custom implementation
 */
public interface IAgriGrowthStage extends IAgriRegisterable<IAgriGrowthStage> {
    /**
     * A list of default AgriCraft growth stages for the desired number of stages.
     * The first is the initial stage, the last is the mature and final stage.
     *
     * @param stages the number of stages required
     * @return list containing IAgriGrowthStages following the default AgriCraft implementation
     */
    @SuppressWarnings("unused")
    static List<IAgriGrowthStage> getDefaults(int stages) {
        return AgriApi.getDefaultGrowthStages(stages);
    }

    /**
     * @return true if the plant can be harvested at this growth stage
     */
    boolean isMature();

    /**
     * @return true if this is a final growth stage (meaning it does not have any further growth stages)
     */
    boolean isFinal();

    /**
     * @return true if a plant of this growth stage will drop its seed when broken
     */
    boolean canDropSeed();

    /**
     * The main decision making method which defines the growth scheme.
     * Can return any growth stage, as long as it is compatible with the growth scheme (defined by the plant or weed)
     *
     * This method is called after a successful growth tick for a plant or weed, note that one should not do any checks
     * on the crop object regarding fertility, as this is already done before this method is fired.
     *
     * @param crop the crop which plant or weeds currently have this growth stage for which the growth tick passed
     * @param random a pseudo-random generator to make decisions
     * @return The next growth stage after a successful growth tick (returns itself if this is the final growth stage).
     */
    @Nonnull
    IAgriGrowthStage getNextStage(IAgriCrop crop, Random random);

    /**
     * Similar as getNextStage(), but returning a previous growth stage instead
     * Can return any growth stage, as long as it is compatible with the growth scheme (defined by the plant or weed)
     *
     * This method is used for reversing growth, such as clipping, or due to certain weeds.
     *
     * @param crop the crop which plant or weeds which currently have this growth stage
     * @param random a pseudo-random generator to make decisions
     * @return The next growth stage before the current growth stage (returns itself if this is the initial growth stage).
     */
    @Nonnull
    IAgriGrowthStage getPreviousStage(IAgriCrop crop, Random random);

    /**
     * @return the growth percentage corresponding to this growth stage, between 0 and 1 (both inclusive),
     * in which 1 means mature
     */
    double growthPercentage();

    /**
     * Internal only, used for the default no growth stage property when no plant is planted
     * @return if this is an actual growth stage
     */
    default boolean isGrowthStage() {
        return true;
    }
}
