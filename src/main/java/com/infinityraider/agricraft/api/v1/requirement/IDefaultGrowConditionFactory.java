package com.infinityraider.agricraft.api.v1.requirement;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * Class to obtain default AgriCraft implementations of the most common IGrowConditions.
 * Its instance can be retrieved via AgriApi.getDefaultGrowConditionFactory
 *
 * The strength predicates define strength levels at which the condition can be ignored
 */
@SuppressWarnings("unused")
public interface IDefaultGrowConditionFactory {
    /**
     * @return the AgriCraft IDefaultGrowConditionFactory instance
     */
    static IDefaultGrowConditionFactory getInstance() {
        return AgriApi.getDefaultGrowConditionFactory();
    }

    /*
     * ----
     * soil
     * ----
     */

    /**
     * General soil growth condition
     */
    IAgriGrowCondition soil(BiFunction<Integer, IAgriSoil, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * General soil humidity growth condition
     */
    IAgriGrowCondition soilHumidity(BiFunction<Integer, IAgriSoil.Humidity, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * General soil acidity growth condition
     */
    IAgriGrowCondition soilAcidity(BiFunction<Integer, IAgriSoil.Acidity, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * General soil nutrients growth condition
     */
    IAgriGrowCondition soilNutrients(BiFunction<Integer, IAgriSoil.Nutrients, IAgriGrowthResponse> response, List<Component> tooltips);


    /*
     * -----
     * light
     * -----
     */

    /**
     * general light growth condition
     */
    IAgriGrowCondition light(BiFunction<Integer, Integer, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * fertile when the predicate (strength, light) is met, infertile otherwise
     */
    IAgriGrowCondition light(BiPredicate<Integer, Integer> strengthLightPredicate);


    /*
     * --------
     * redstone
     * --------
     */

    /**
     * general redstone growth condition
     */
    IAgriGrowCondition redstone(BiFunction<Integer, Integer, IAgriGrowthResponse> predicate, List<Component> tooltips);

    /**
     * fertile when the predicate (strength, redstone) is met, infertile otherwise
     */
    IAgriGrowCondition redstone(BiPredicate<Integer, Integer> strengthRedstonePredicate);


    /*
     * ------
     * liquid
     * ------
     */

    /**
     * general fluid growth condition
     */
    IAgriGrowCondition fluid(BiFunction<Integer, Fluid, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * general fluid state growth condition
     */
    IAgriGrowCondition fluidState(BiFunction<Integer, FluidState, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * general fluid class growth condition
     */
    IAgriGrowCondition fluidClass(BiFunction<Integer, Class<? extends Fluid>, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * fertile if the fluid matches or if the strength predicate passes, infertile otherwise
     */
    IAgriGrowCondition fluid(IntPredicate strength, Fluid fluid);

    /**
     * fertile if the fluid state matches or if the strength predicate passes, infertile otherwise
     */
    IAgriGrowCondition fluidState(IntPredicate strength, FluidState fluid);

    /**
     * fertile if the fluid class matches or if the strength predicate passes, infertile otherwise
     */
    IAgriGrowCondition fluidClass(IntPredicate strength, Class<? extends Fluid> fluid, List<Component> tooltips);

    /**
     * fertile if any of the fluids match, or if the strength predicate passes, infertile otherwise
     */
    default IAgriGrowCondition fluids(IntPredicate strength, Fluid... fluids) {
        return this.fluids(strength, Arrays.asList(fluids));
    }

    /**
     * fertile if any of the fluids match, or if the strength predicate passes, infertile otherwise
     */
    default IAgriGrowCondition fluidStates(IntPredicate strength, FluidState... fluids) {
        return this.fluidStates(strength, Arrays.asList(fluids));
    }

    /**
     * fertile if any of the fluids match, or if the strength predicate passes, infertile otherwise
     */
    IAgriGrowCondition fluids(IntPredicate strength, Collection<Fluid> fluids);

    /**
     * fertile if any of the fluids match, or if the strength predicate passes, infertile otherwise
     */
    IAgriGrowCondition fluidStates(IntPredicate strength, Collection<FluidState> fluids);


    /*
     * -----
     * biome
     * -----
     */

    /**
     * general biome growth condition
     */
    IAgriGrowCondition biome(BiFunction<Integer, Biome, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * general biome category growth condition
     */
    IAgriGrowCondition biomeCategory(BiFunction<Integer, Biome.BiomeCategory, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * fertile if in the biome, or if the strength predicate passes
     */
    IAgriGrowCondition biome(IntPredicate strength, Biome biome, Component biomeName);

    /**
     * fertile if in the biome category, or if the strength predicate passes
     */
    IAgriGrowCondition biomeCategory(IntPredicate strength, Biome.BiomeCategory category, Component categoryName);

    /**
     * fertile if in any of the biomes, or if the strength predicate passes
     */
    default IAgriGrowCondition biomes(IntPredicate strength, Function<Biome, Component> nameFunction, Biome... biomes) {
        return this.biomes(strength, Arrays.asList(biomes), nameFunction);
    }

    /**
     * fertile if in any of the biome category, or if the strength predicate passes
     */
    default IAgriGrowCondition biomeCategories(IntPredicate strength, Function<Biome.BiomeCategory, Component> nameFunction, Biome.BiomeCategory... categories) {
        return this.biomeCategories(strength, Arrays.asList(categories), nameFunction);
    }

    /**
     * fertile if in any of the biomes, or if the strength predicate passes
     */
    IAgriGrowCondition biomes(IntPredicate strength, Collection<Biome> biomes, Function<Biome, Component> nameFunction);

    /**
     * fertile if in any of the biome category, or if the strength predicate passes
     */
    IAgriGrowCondition biomeCategories(IntPredicate strength, Collection<Biome.BiomeCategory> categories, Function<Biome.BiomeCategory, Component> nameFunction);


    /*
     * -------
     * climate
     * -------
     */

    /**
     * general climate growth condition
     */
    IAgriGrowCondition climate(BiFunction<Integer, Biome.ClimateSettings, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * fertile if the climate matches, or if the strength predicate passes
     */
    IAgriGrowCondition climate(IntPredicate strength, Biome.ClimateSettings climate, List<Component> tooltips);

    /**
     * fertile if any of the climates match, or if the strength predicate passes
     */
    default IAgriGrowCondition climates(IntPredicate strength, List<Component> tooltips, Biome.ClimateSettings... climates) {
        return this.climates(strength, Arrays.asList(climates), tooltips);
    }

    /**
     * fertile if any of the climates match, or if the strength predicate passes
     */
    IAgriGrowCondition climates(IntPredicate strength, Collection<Biome.ClimateSettings> climates, List<Component> tooltips);


    /*
     * ---------
     * dimension
     * ---------
     */

    /**
     * general dimension growth condition (by registry key)
     */
    IAgriGrowCondition dimensionFromKey(BiFunction<Integer, ResourceKey<Level>, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * general dimension growth condition (by dimension type)
     */
    IAgriGrowCondition dimensionFromType(BiFunction<Integer, DimensionType, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * fertile if the dimension registry key matches, or if the strength predicate passes
     */
    IAgriGrowCondition dimension(IntPredicate strength, ResourceKey<Level> dimension, Component dimensionName);

    /**
     * fertile if the dimension type matches, or if the strength predicate passes
     */
    IAgriGrowCondition dimension(IntPredicate strength, DimensionType dimension, Component dimensionName);


    /*
     * -----
     * weeds
     * -----
     */

    /**
     * general weed growth condition
     */
    IAgriGrowCondition weed(BiFunction<Integer, IAgriWeed, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * Fertile if there is any weed, or if the strength predicate passes
     */
    IAgriGrowCondition withWeed(IntPredicate strength);

    /**
     * Fertile if there is a specific weed, or if the strength predicate passes
     */
    IAgriGrowCondition withWeed(IntPredicate strength, IAgriWeed weed);

    /**
     * Fertile if there is no weed, or if the strength predicate passes
     */
    IAgriGrowCondition withoutWeed(IntPredicate strength);

    /**
     * Fertile if there no specific weed, or if the strength predicate passes
     */
    IAgriGrowCondition withoutWeed(IntPredicate strength, IAgriWeed weed);


    /*
     * -----
     * time
     * -----
     */

    /**
     * general time growth condition
     */
    IAgriGrowCondition time(BiFunction<Integer, Long, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * fertile during the day, or if the strength predicate passes
     */
    IAgriGrowCondition day(IntPredicate strength);

    /**
     * fertile during dusk, or if the strength predicate passes
     */
    IAgriGrowCondition dusk(IntPredicate strength);

    /**
     * fertile during the night, or if the strength predicate passes
     */
    IAgriGrowCondition night(IntPredicate strength);

    /**
     * fertile during dawn, or if the strength predicate passes
     */
    IAgriGrowCondition dawn(IntPredicate strength);


    /*
     * ------
     * block below
     * ------
     */

    /**
     * general block below growth condition
     */
    IAgriGrowCondition blockBelow(BiFunction<Integer, Block, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * general block state below growth condition
     */
    IAgriGrowCondition stateBelow(BiFunction<Integer, BlockState, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * general block class below growth condition
     */
    IAgriGrowCondition classBelow(BiFunction<Integer, Class<? extends Block>, IAgriGrowthResponse> response, List<Component> tooltips);


    /*
     * -------------
     * blocks nearby
     * -------------
     */

    /**
     * general blocks nearby growth condition
     */
    IAgriGrowCondition blocksNearby(BiFunction<Integer, Stream<Block>, IAgriGrowthResponse> response,
                                    BlockPos minOffset, BlockPos maxOffset, List<Component> tooltips);

    /**
     * general block states nearby growth condition
     */
    IAgriGrowCondition blockStatesNearby(BiFunction<Integer, Stream<BlockState>, IAgriGrowthResponse> response,
                                         BlockPos minOffset, BlockPos maxOffset, List<Component> tooltips);

    /**
     * general tile entities nearby growth condition
     */
    IAgriGrowCondition tileEntitiesNearby(BiFunction<Integer, Stream<BlockEntity>, IAgriGrowthResponse> response,
                                          BlockPos minOffset, BlockPos maxOffset, List<Component> tooltips);

    /**
     * general block classes nearby growth condition
     */
    IAgriGrowCondition classNearby(BiFunction<Integer, Stream<Class<? extends Block>>, IAgriGrowthResponse> response,
                                   BlockPos minOffset, BlockPos maxOffset, List<Component> tooltips);

    /**
     * fertile if there is at least a specified amount of a certain block nearby
     */
    IAgriGrowCondition blockNearby(Block block, int amount, BlockPos minOffset, BlockPos maxOffset);

    /**
     * fertile if there is at least a specified amount of a certain block state nearby
     */
    IAgriGrowCondition blockStateNearby(BlockState state, int amount, BlockPos minOffset, BlockPos maxOffset);

    /**
     * fertile if there is at least a specified amount of a certain tile entity nearby
     */
    IAgriGrowCondition tileEntityNearby(Predicate<CompoundTag> filter, int amount, BlockPos minOffset, BlockPos maxOffset);

    /**
     * fertile if there is at least a specified amount of certain blocks nearby
     */
    IAgriGrowCondition blocksNearby(Collection<Block> blocks, int amount, BlockPos minOffset, BlockPos maxOffset);

    /**
     * fertile if there is at least a specified amount of certain block states nearby
     */
    IAgriGrowCondition blockStatesNearby(Collection<BlockState> states, int amount, BlockPos minOffset, BlockPos maxOffset);

    /**
     * fertile if there is at least a specified amount of certain tile entities nearby
     */
    IAgriGrowCondition tileEntitiesNearby(Collection<Predicate<CompoundTag>> filters, int amount, BlockPos minOffset, BlockPos maxOffset);


    /*
     * -------------
     * entity nearby
     * -------------
     */

    /**
     * general entities nearby growth condition
     */
    IAgriGrowCondition entitiesNearby(BiFunction<Integer, Stream<Entity>, IAgriGrowthResponse> response, double range, List<Component> tooltips);

    /**
     * fertile if a certain number (depending on the strength) of a specific entity is nearby
     */
    IAgriGrowCondition entityNearby(IntUnaryOperator strengthToAmount, EntityType<?> entityType, double range);

    /**
     * fertile if a certain number (depending on the strength) of a specific entity is nearby
     */
    IAgriGrowCondition entityNearby(IntUnaryOperator strengthToAmount, Class<? extends Entity> entityClass, double range, Component entityName);

    /*
     * ----
     * rain
     * ----
     */

    /**
     * general rain growth condition
     */
    IAgriGrowCondition rain(BiFunction<Integer, Boolean, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * fertile when it is not raining, or if the strength predicate is met
     */
    IAgriGrowCondition noRain(IntPredicate strength);

    /**
     * fertile when it is raining, or if the strength predicate is met
     */
    IAgriGrowCondition inRain(IntPredicate strength);

    /*
     * ----
     * snow
     * ----
     */

    /**
     * general snow growth condition
     */
    IAgriGrowCondition snow(BiFunction<Integer, Boolean, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * fertile when it is not snowing, or if the strength predicate is met
     */
    IAgriGrowCondition noSnow(IntPredicate strength);

    /**
     * fertile when it is snowing, or if the strength predicate is met
     */
    IAgriGrowCondition inSnow(IntPredicate strength);


    /*
     * -------
     * seasons
     * -------
     */

    /**
     * General season growth condition
     */
    IAgriGrowCondition season(BiFunction<Integer, AgriSeason, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * fertile during the season, or if the strength predicate is met
     */
    IAgriGrowCondition season(IntPredicate strength, AgriSeason season);

    /**
     * fertile during any of the seasons, or if the strength predicate is met
     */
    default IAgriGrowCondition seasons(IntPredicate strength, AgriSeason... seasons) {
        return this.seasons(strength, Arrays.asList(seasons));
    }

    /**
     * fertile during any of the seasons, or if the strength predicate is met
     */
    IAgriGrowCondition seasons(IntPredicate strength, Collection<AgriSeason> seasons);

    /*
     * ----------
     * structures
     * ----------
     */

    /**
     * General structure growth condition
     */
    IAgriGrowCondition structure(BiFunction<Integer, Stream<StructureFeature<?>>, IAgriGrowthResponse> response, List<Component> tooltips);

    /**
     * Fertile if in a village (or if the strength predicate passes)
     */
    IAgriGrowCondition inVillage(IntPredicate strength);

    /**
     * Fertile if in a pillager outpost (or if the strength predicate passes)
     */
    IAgriGrowCondition inPillagerOutpost(IntPredicate strength);

    /**
     * Fertile if in a mineshaft (or if the strength predicate passes)
     */
    IAgriGrowCondition inMineshaft(IntPredicate strength);

    /**
     * Fertile if in a mansion (or if the strength predicate passes)
     */
    IAgriGrowCondition inMansion(IntPredicate strength);

    /**
     * Fertile if in a pyramid (jungle or desert, or if the strength predicate passes)
     */
    IAgriGrowCondition inPyramid(IntPredicate strength);

    /**
     * Fertile if in a jungle pyramid (or if the strength predicate passes)
     */
    IAgriGrowCondition inJunglePyramid(IntPredicate strength);

    /**
     * Fertile if in a desert pyramid (or if the strength predicate passes)
     */
    IAgriGrowCondition inDesertPyramid(IntPredicate strength);

    /**
     * Fertile if in an igloo (or if the strength predicate passes)
     */
    IAgriGrowCondition inIgloo(IntPredicate strength);

    /**
     * Fertile if in a ruined portal (or if the strength predicate passes)
     */
    IAgriGrowCondition inRuinedPortal(IntPredicate strength);

    /**
     * Fertile if in a shipwreck (or if the strength predicate passes)
     */
    IAgriGrowCondition inShipwreck(IntPredicate strength);

    /**
     * Fertile if in a swamp hut (or if the strength predicate passes)
     */
    IAgriGrowCondition inSwampHut(IntPredicate strength);

    /**
     * Fertile if in a stronghold (or if the strength predicate passes)
     */
    IAgriGrowCondition inStronghold(IntPredicate strength);

    /**
     * Fertile if in a monument (or if the strength predicate passes)
     */
    IAgriGrowCondition inMonument(IntPredicate strength);

    /**
     * Fertile if in an ocean ruin (or if the strength predicate passes)
     */
    IAgriGrowCondition inOceanRuin(IntPredicate strength);

    /**
     * Fertile if in a fortress (or if the strength predicate passes)
     */
    IAgriGrowCondition inFortress(IntPredicate strength);

    /**
     * Fertile if in an end city (or if the strength predicate passes)
     */
    IAgriGrowCondition inEndCity(IntPredicate strength);

    /**
     * Fertile if in a buried treasure (or if the strength predicate passes)
     */
    IAgriGrowCondition inBuriedTreasure(IntPredicate strength);

    /**
     * Fertile if in a nether fossil (or if the strength predicate passes)
     */
    IAgriGrowCondition inNetherFossil(IntPredicate strength);

    /**
     * Fertile if in a bastion remnant (or if the strength predicate passes)
     */
    IAgriGrowCondition inBastionRemnant(IntPredicate strength);

    /**
     * Infertile if in a village (unless the strength predicate passes)
     */
    IAgriGrowCondition notInVillage(IntPredicate strength);

    /**
     * Infertile if in a pillager outpost (unless the strength predicate passes)
     */
    IAgriGrowCondition notInPillagerOutpost(IntPredicate strength);

    /**
     * Infertile if in a mineshaft (unless the strength predicate passes)
     */
    IAgriGrowCondition notInMineshaft(IntPredicate strength);

    /**
     * Infertile if in a mansion (unless the strength predicate passes)
     */
    IAgriGrowCondition notInMansion(IntPredicate strength);

    /**
     * Infertile if in a pyramid (jungle or desert, unless the strength predicate passes)
     */
    IAgriGrowCondition notInPyramid(IntPredicate strength);

    /**
     * Infertile if in a jungle pyramid (unless the strength predicate passes)
     */
    IAgriGrowCondition notInJunglePyramid(IntPredicate strength);

    /**
     * Infertile if in a desert pyramid (unless the strength predicate passes)
     */
    IAgriGrowCondition notInDesertPyramid(IntPredicate strength);

    /**
     * Infertile if in an igloo (unless the strength predicate passes)
     */
    IAgriGrowCondition notInIgloo(IntPredicate strength);

    /**
     * Infertile if in a ruined portal (unless the strength predicate passes)
     */
    IAgriGrowCondition notInRuinedPortal(IntPredicate strength);

    /**
     * Infertile if in a shipwreck (unless the strength predicate passes)
     */
    IAgriGrowCondition notInShipwreck(IntPredicate strength);

    /**
     * Infertile if in a swamp hut (unless the strength predicate passes)
     */
    IAgriGrowCondition notInSwampHut(IntPredicate strength);

    /**
     * Infertile if in a stronghold (unless the strength predicate passes)
     */
    IAgriGrowCondition notInStronghold(IntPredicate strength);

    /**
     * Infertile if in a monument (unless the strength predicate passes)
     */
    IAgriGrowCondition notInMonument(IntPredicate strength);

    /**
     * Infertile if in an ocean ruin (unless the strength predicate passes)
     */
    IAgriGrowCondition notInOceanRuin(IntPredicate strength);

    /**
     * Infertile if in a fortress (unless the strength predicate passes)
     */
    IAgriGrowCondition notInFortress(IntPredicate strength);

    /**
     * Infertile if in an end city (unless the strength predicate passes)
     */
    IAgriGrowCondition notInEndCity(IntPredicate strength);

    /**
     * Infertile if in a buried treasure (unless the strength predicate passes)
     */
    IAgriGrowCondition notInBuriedTreasure(IntPredicate strength);

    /**
     * Infertile if in a nether fossil (unless the strength predicate passes)
     */
    IAgriGrowCondition notInNetherFossil(IntPredicate strength);

    /**
     * Infertile if in a bastion remnant (unless the strength predicate passes)
     */
    IAgriGrowCondition notInBastionRemnant(IntPredicate strength);

    /**
     * Fertile if in the given structure (or if the strength predicate passes)
     */
    IAgriGrowCondition inStructure(IntPredicate strength, StructureFeature<?> structure, Component structureName);

    /**
     * Infertile if in the given structure (unless the strength predicate passes)
     */
    IAgriGrowCondition notInStructure(IntPredicate strength, StructureFeature<?> structure, Component structureName);
}
