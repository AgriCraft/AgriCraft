package com.infinityraider.agricraft.api.v1.requirement;

import com.infinityraider.agricraft.api.v1.AgriApi;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * Encapsulating interface for growth conditions, all conditions should be returned by getGrowConditions(IAgriGrowthStage).
 *
 * This interface contains additional methods for individual checks for humidity, acidity, nutrients and light level,
 * these are used to provide feedback to the player, such as in the in-game journal or JEI.
 */
public interface IAgriGrowthRequirement {

    /**
     * Gets all the grow conditions that must be met for a plant to be able to grow
     *
     * The AgriCraft API ships a Factory class to construct default instances for the most common growth conditions
     * See IDefaultGrowConditionFactory
     *
     * @return a set containing all growth conditions for the given stage
     */
    @Nonnull
    Set<IAgriGrowCondition> getGrowConditions();

    /**
     * Specific check for soil humidity
     *
     * @param humidity the humidity
     * @param strength the strength stat of the plant
     * @return if the plant can grow on a soil with the given humidity level
     */
    boolean isSoilHumidityAccepted(IAgriSoil.Humidity humidity, int strength);

    /**
     * Specific check for soil acidity
     *
     * @param acidity the acidity
     * @param strength the strength stat of the plant
     * @return if the plant can grow on a soil with the given humidity level
     */
    boolean isSoilAcidityAccepted(IAgriSoil.Acidity acidity, int strength);

    /**
     * Specific check for soil nutrients
     *
     * @param nutrients the nutrients
     * @param strength the strength stat of the plant
     * @return if the plant can grow on a soil with the given humidity level
     */
    boolean isSoilNutrientsAccepted(IAgriSoil.Nutrients nutrients, int strength);

    /**
     * Specific check for light level
     *
     * @param light level the humidity
     * @param strength the strength stat of the plant
     * @return if the plant can grow on a soil with the given humidity level
     */
    boolean isLightLevelAccepted(int light, int strength);

    /**
     * @return a new Builder object
     */
    static Builder builder() {
        return AgriApi.getGrowthRequirementBuilder();
    }

    /**
     * Builder class to initialize IAgriGrowthRequirements
     */
    interface Builder {
        /**
         * Builds the growth requirement
         *
         * Rules for humidity, acidity, nutrients and light level must be defined before calling build,
         * if not, an IllegalStateException will be thrown.
         *
         * @return a new IAgriGrowthRequirement
         */
        IAgriGrowthRequirement build();

        /**
         * Utility method to quickly fetch the default grow condition factory
         * @return the AgriCraft IDefaultGrowCondition instance
         */
        default IDefaultGrowConditionFactory getFactory() {
            return AgriApi.getDefaultGrowConditionFactory();
        }

        /**
         * Defines the humidity rule, must be defined before calling build()
         * The first argument of the predicate is the humidity, the second the strength of the plant
         *
         * This method will implicitly add the IAgriGrowCondition for the predicate to the Builder as well
         *
         * @param predicate test for humidity
         * @return the builder (this)
         */
        Builder defineHumidity(BiPredicate<Integer, IAgriSoil.Humidity> predicate);

        /**
         * Defines the acidity rule, must be defined before calling build()
         * The first argument of the predicate is the acidity, the second the strength of the plant
         *
         * This method will implicitly add the IAgriGrowCondition for the predicate to the Builder as well
         *
         * @param predicate test for acidity
         * @return the builder (this)
         */
        Builder defineAcidity(BiPredicate<Integer, IAgriSoil.Acidity> predicate);

        /**
         * Defines the nutrients rule, must be defined before calling build()
         * The first argument of the predicate is the nutrients, the second the strength of the plant
         *
         * This method will implicitly add the IAgriGrowCondition for the predicate to the Builder as well
         *
         * @param predicate test for nutrients
         * @return the builder (this)
         */
        Builder defineNutrients(BiPredicate<Integer, IAgriSoil.Nutrients> predicate);

        /**
         * Defines the light level rule, must be defined before calling build()
         * The first argument of the predicate is the light level, the second the strength of the plant
         *
         * This method will implicitly add the IAgriGrowCondition for the predicate to the Builder as well
         *
         * @param predicate test for light level
         * @return the builder (this)
         */
        Builder defineLightLevel(BiPredicate<Integer, Integer> predicate);

        /**
         * Adds a grow condition to the builder
         * @param condition the condition to add
         * @return the builder (this)
         */
        Builder addCondition(IAgriGrowCondition condition);
    }
}
