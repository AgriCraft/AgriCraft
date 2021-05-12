package com.infinityraider.agricraft.api.v1.requirement;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
     * @return if the plant can grow on a soil with the given humidity
     */
    boolean isSoilHumidityAccepted(IAgriSoil.Humidity humidity, int strength);

    /**
     * Specific check for soil acidity
     *
     * @param acidity the acidity
     * @param strength the strength stat of the plant
     * @return if the plant can grow on a soil with the given acidity
     */
    boolean isSoilAcidityAccepted(IAgriSoil.Acidity acidity, int strength);

    /**
     * Specific check for soil nutrients
     *
     * @param nutrients the nutrients
     * @param strength the strength stat of the plant
     * @return if the plant can grow on a soil with the given nutrients
     */
    boolean isSoilNutrientsAccepted(IAgriSoil.Nutrients nutrients, int strength);

    /**
     * Specific check for light level
     *
     * @param light the light level
     * @param strength the strength stat of the plant
     * @return if the plant can grow with the given light level
     */
    boolean isLightLevelAccepted(int light, int strength);

    /**
     * Specific check for seasons
     * @param season the season
     * @param strength the strength stat of the plant
     * @return if the plant can grow during the given season
     */
    boolean isSeasonAccepted(AgriSeason season, int strength);

    /**
     * Checks if this growth requirement is met for a given crop
     * @param crop the crop
     * @return true if the growth requirement is met
     */
    default boolean isMet(IAgriCrop crop) {
        return this.isMet(crop, crop.getStats().getStrength());
    }

    /**
     * Checks if this growth requirement would be met for a given crop with a certain strength level
     * @param crop the crop
     * @param strength the strength
     * @return true if the growth requirement is met
     */
    default boolean isMet(IAgriCrop crop, final int strength) {
        final World world = crop.world();
        if(world == null) {
            return false;
        }
        final BlockPos pos = crop.getPosition();
        return this.isMet(world, pos, strength);
    }

    /**
     * Checks if this growth requirement is met at a given position in the world for a certain strength level
     * @param world the world
     * @param pos the position
     * @param strength the strength
     * @return true if the growth requirement is met
     */
    default boolean isMet(World world, BlockPos pos, int strength) {
        return this.getGrowConditions().stream().allMatch(condition -> condition.isMet(world, pos, strength));
    }

    /**
     * @return a new Builder object
     */
    static Builder builder() {
        return AgriApi.getGrowthRequirementBuilder();
    }

    /**
     * Builder class to initialize IAgriGrowthRequirements
     *
     * Note that the Builder is also an instance of IDefaultGrowConditionFactory for easy creation of Growth Conditions
     */
    interface Builder extends IDefaultGrowConditionFactory {
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
         * Defines the seasonality rule, must be defined before calling build()
         * The first argument of the predicate is the light level, the second the strength of the plant
         *
         * This method will implicitly add the IAgriGrowCondition for the predicate to the Builder as well
         *
         * @param predicate test for seasons
         * @return the builder (this)
         */
        Builder defineSeasonality(BiPredicate<Integer, AgriSeason> predicate);

        /**
         * Adds a grow condition to the builder
         * @param condition the condition to add
         * @return the builder (this)
         */
        Builder addCondition(IAgriGrowCondition condition);
    }
}
