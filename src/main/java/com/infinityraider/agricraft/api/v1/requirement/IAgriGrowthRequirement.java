package com.infinityraider.agricraft.api.v1.requirement;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.BiFunction;

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
    IAgriGrowthResponse getSoilHumidityResponse(IAgriSoil.Humidity humidity, int strength);

    /**
     * Specific check for soil acidity
     *
     * @param acidity the acidity
     * @param strength the strength stat of the plant
     * @return if the plant can grow on a soil with the given acidity
     */
    IAgriGrowthResponse getSoilAcidityResponse(IAgriSoil.Acidity acidity, int strength);

    /**
     * Specific check for soil nutrients
     *
     * @param nutrients the nutrients
     * @param strength the strength stat of the plant
     * @return if the plant can grow on a soil with the given nutrients
     */
    IAgriGrowthResponse getSoilNutrientsResponse(IAgriSoil.Nutrients nutrients, int strength);

    /**
     * Specific check for light level
     *
     * @param light the light level
     * @param strength the strength stat of the plant
     * @return if the plant can grow with the given light level
     */
    IAgriGrowthResponse getLightLevelResponse(int light, int strength);

    /**
     * Specific check for biomes
     *
     * @param biome the biome
     * @param strength the strength stat of the plant
     * @return if the plant can grow in the given biome
     */
    IAgriGrowthResponse getBiomeResponse(Biome biome, int strength);

    /**
     * Specific check for dimensions
     *
     * @param dimension the dimension
     * @param strength the strength stat of the plant
     * @return if the plant can grow in the given dimension
     */
    IAgriGrowthResponse getDimensionResponse(DimensionType dimension, int strength);

    /**
     * Specific check for seasons
     * @param season the season
     * @param strength the strength stat of the plant
     * @return if the plant can grow during the given season
     */
    IAgriGrowthResponse getSeasonResponse(AgriSeason season, int strength);

    /**
     * Specific check for fluids
     * @param fluid the fluid
     * @param strength the strength
     * @return how the plant behaves in the fluid
     */
    IAgriGrowthResponse getFluidResponse(Fluid fluid, int strength);

    /**
     * Checks the growth requirement for a given crop
     * @param crop the crop
     * @return the growth response
     */
    default IAgriGrowthResponse check(IAgriCrop crop) {
        return this.check(crop, crop.getStats().getStrength());
    }

    /**
     * Checks the growth requirement for a given crop with a certain strength level
     * @param crop the crop
     * @param strength the strength
     * @return the growth response
     */
    default IAgriGrowthResponse check(IAgriCrop crop, final int strength) {
        final Level world = crop.world();
        if(world == null) {
            return IAgriGrowthResponse.INFERTILE;
        }
        final BlockPos pos = crop.getPosition();
        return this.check(crop, world, pos, strength);
    }

    /**
     * Checks the growth requirement for a crop at a given position in the world for a certain strength level
     *
     * @param crop the crop
     * @param world the world
     * @param pos the position
     * @param strength the strength
     * @return the growth response
     */
    default IAgriGrowthResponse check(IAgriCrop crop, Level world, BlockPos pos, int strength) {
        return this.getGrowConditions().stream()
                .map(condition -> condition.check(crop, world, pos, strength))
                // filter out the fertile ones to avoid additional checks in the collection
                .filter(response -> !response.isFertile())
                .collect(IAgriGrowthResponse.COLLECTOR);
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
         * @return a new IAgriGrowthRequirement
         */
        IAgriGrowthRequirement build();

        /**
         * Defines the humidity rule, if not called before calling build(), any humidity will be allowed
         * The first argument of the function is the humidity, the second the strength of the plant
         *
         * This method will implicitly add the IAgriGrowCondition for the predicate to the Builder as well
         *
         * @param response test for humidity
         * @return the builder (this)
         */
        Builder defineHumidity(BiFunction<Integer, IAgriSoil.Humidity, IAgriGrowthResponse> response);

        /**
         * Defines the acidity rule, if not called before calling build(), any acidity will be allowed
         * The first argument of the function is the acidity, the second the strength of the plant
         *
         * This method will implicitly add the IAgriGrowCondition for the predicate to the Builder as well
         *
         * @param response test for acidity
         * @return the builder (this)
         */
        Builder defineAcidity(BiFunction<Integer, IAgriSoil.Acidity, IAgriGrowthResponse> response);

        /**
         * Defines the nutrients rule, if not called before calling build(), any nutrients will be allowed
         * The first argument of the function is the nutrients, the second the strength of the plant
         *
         * This method will implicitly add the IAgriGrowCondition for the predicate to the Builder as well
         *
         * @param response test for nutrients
         * @return the builder (this)
         */
        Builder defineNutrients(BiFunction<Integer, IAgriSoil.Nutrients, IAgriGrowthResponse> response);

        /**
         * Defines the light level rule, if not called before calling build(), any light level will be allowed
         * The first argument of the function is the light level, the second the strength of the plant
         *
         * This method will implicitly add the IAgriGrowCondition for the predicate to the Builder as well
         *
         * @param response test for light level
         * @return the builder (this)
         */
        Builder defineLightLevel(BiFunction<Integer, Integer, IAgriGrowthResponse> response);

        /**
         * Defines the fluid rule, if not called before calling build(), any fluid will be allowed
         * The first argument of the function is the light level, the second the strength of the plant
         *
         * This method will implicitly add the IAgriGrowCondition for the predicate to the Builder as well
         *
         * @param response test for light level
         * @return the builder (this)
         */
        Builder defineFluid(BiFunction<Integer, Fluid, IAgriGrowthResponse> response);

        /**
         * Defines the biome rule, if not called before calling build(), any biome will be allowed
         * The first argument of the function is the biome, the second the strength of the plant
         *
         * This method will implicitly add the IAgriGrowCondition for the predicate to the Builder as well
         *
         * @param response test for light level
         * @return the builder (this)
         */
        Builder defineBiome(BiFunction<Integer, Biome, IAgriGrowthResponse> response);

        /**
         * Defines the dimension rule, if not called before calling build(), any dimension will be allowed
         * The first argument of the function is the dimension, the second the strength of the plant
         *
         * This method will implicitly add the IAgriGrowCondition for the predicate to the Builder as well
         *
         * @param response test for light level
         * @return the builder (this)
         */
        Builder defineDimension(BiFunction<Integer, DimensionType, IAgriGrowthResponse> response);

        /**
         * Defines the seasonality rule, must be defined before calling build()
         * The first argument of the function is the light level, the second the strength of the plant
         *
         * This method will implicitly add the IAgriGrowCondition for the predicate to the Builder as well
         *
         * @param response test for seasons
         * @return the builder (this)
         */
        Builder defineSeasonality(BiFunction<Integer, AgriSeason, IAgriGrowthResponse> response);

        /**
         * Adds a grow condition to the builder
         * @param condition the condition to add
         * @return the builder (this)
         */
        Builder addCondition(IAgriGrowCondition condition);
    }
}
