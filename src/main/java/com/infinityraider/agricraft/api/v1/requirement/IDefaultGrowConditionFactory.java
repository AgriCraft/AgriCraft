package com.infinityraider.agricraft.api.v1.requirement;

import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.Tag;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.extensions.IForgeStructure;
import net.minecraftforge.common.util.TriPredicate;

import java.util.Collection;
import java.util.List;
import java.util.function.*;

/**
 * Class to obtain default AgriCraft implementations of the most common IGrowConditions.
 * Its instance can be retrieved via AgriApi.getDefaultGrowConditionFactory
 *
 * The strength predicates define strength levels at which the condition can be ignored
 */
@SuppressWarnings("unused")
public interface IDefaultGrowConditionFactory {
    /*
     * ----
     * soil
     * ----
     */

    /** Must match the given soil */
    IAgriGrowCondition soil(IntPredicate strength, IAgriSoil soil);

    /** Must match any of the given soils */
    IAgriGrowCondition soil(IntPredicate strength, IAgriSoil... soils);

    /** Must match any of the given soils */
    IAgriGrowCondition soil(IntPredicate strength, Collection<IAgriSoil> soils);

    /** Must match the soil predicate */
    IAgriGrowCondition soil(BiPredicate<Integer, IAgriSoil> predicate, List<ITextComponent> tooltips);

    /** Must match the given soil humidity */
    IAgriGrowCondition soilHumidity(IntPredicate strength, IAgriSoil.Humidity humidity);

    /** Must match any of the given soil humidities */
    IAgriGrowCondition soilHumidities(IntPredicate strength, IAgriSoil.Humidity... humidities);

    /** Must match any of the given soil humidities */
    IAgriGrowCondition soilHumidities(IntPredicate strength, Collection<IAgriSoil.Humidity> humidities);

    /** Must match the soil humidity predicate */
    IAgriGrowCondition soilHumidity(BiPredicate<Integer, IAgriSoil.Humidity> predicate, List<ITextComponent> tooltips);

    /** Must match the given soil acidity */
    IAgriGrowCondition soilAcidity(IntPredicate strength, IAgriSoil.Acidity acidity);

    /** Must match any of the given soil acidities */
    IAgriGrowCondition soilAcidities(IntPredicate strength, IAgriSoil.Acidity... acidities);

    /** Must match any of the given soil acidities */
    IAgriGrowCondition soilAcidities(IntPredicate strength, Collection<IAgriSoil.Acidity> acidities);

    /** Must match the soil acidity predicate */
    IAgriGrowCondition soilAcidity(BiPredicate<Integer, IAgriSoil.Acidity> predicate, List<ITextComponent> tooltips);

    /** Must match the given soil nutrients */
    IAgriGrowCondition soilNutrients(IntPredicate strength, IAgriSoil.Nutrients nutrients);

    /** Must match any of the given soil nutrients */
    IAgriGrowCondition soilNutrients(IntPredicate strength, IAgriSoil.Nutrients... nutrients);

    /** Must match any of the given soil nutrients */
    IAgriGrowCondition soilNutrients(IntPredicate strength, Collection<IAgriSoil.Nutrients> nutrients);

    /** Must match the soil nutrients predicate */
    IAgriGrowCondition soilNutrients(BiPredicate<Integer, IAgriSoil.Nutrients> predicate, List<ITextComponent> tooltips);


    /*
     * -----
     * light
     * -----
     */

    /** light value must be between the min and max (both inclusive) */
    IAgriGrowCondition light(IntPredicate strength, int min, int max);

    /** light value must be the given value */
    IAgriGrowCondition light(IntPredicate strength, int value);

    /** Must match the predicate */
    IAgriGrowCondition light(BiPredicate<Integer, Integer> predicate, List<ITextComponent> tooltips);


    /*
     * --------
     * redstone
     * --------
     */

    /** redstone signal must be between the min and max (both inclusive) */
    IAgriGrowCondition redstone(IntPredicate strength, int min, int max);

    /** redstone signal must be the given value */
    IAgriGrowCondition redstone(IntPredicate strength, int value);

    /** Must match the predicate */
    IAgriGrowCondition redstone(BiPredicate<Integer, Integer> predicate, List<ITextComponent> tooltips);


    /*
     * ------
     * liquid
     * ------
     */

    /** Must match the given fluid */
    IAgriGrowCondition liquidFromFluid(IntPredicate strength, Fluid fluid);

    /** Must match any of the given fluids */
    IAgriGrowCondition liquidFromFluid(IntPredicate strength, Fluid... fluids);

    /** Must match any of the given fluids */
    IAgriGrowCondition liquidFromFluid(IntPredicate strength, Collection<Fluid> fluids);

    /** Must match the predicate */
    IAgriGrowCondition liquidFromFluid(BiPredicate<Integer, Fluid> predicate, List<ITextComponent> tooltips);

    /** Must match the given fluid state */
    IAgriGrowCondition liquidFromState(IntPredicate strength, FluidState state);

    /** Must match any of the given fluid states */
    IAgriGrowCondition liquidFromState(IntPredicate strength, FluidState... states);

    /** Must match any of the given fluid states */
    IAgriGrowCondition liquidFromState(IntPredicate strength, Collection<FluidState> states);

    /** Must match the predicate */
    IAgriGrowCondition liquidFromState(BiPredicate<Integer, FluidState> predicate, List<ITextComponent> tooltips);

    /** Must match the tag */
    IAgriGrowCondition liquidFromTag(IntPredicate strength, Tag<Fluid> fluid);

    /** Must match any of the given tags */
    IAgriGrowCondition liquidFromTag(IntPredicate strength, Tag<Fluid>... fluids);

    /** Must match any of the given tags */
    IAgriGrowCondition liquidFromTag(IntPredicate strength, Collection<Tag<Fluid>> fluids);

    /** Must be instance of the given class */
    IAgriGrowCondition liquidFromClass(IntPredicate strength, Class<Fluid> fluid, List<ITextComponent> tooltips);

    /** Must be instance of any of the given classes */
    IAgriGrowCondition liquidFromClass(IntPredicate strength, List<ITextComponent> tooltips, Class<Fluid>... fluids);

    /** Must be instance of any of the given classes */
    IAgriGrowCondition liquidFromClass(IntPredicate strength, Collection<Class<Fluid>> fluids, List<ITextComponent> tooltips);

    /** Must match the predicate */
    IAgriGrowCondition liquidFromClass(BiPredicate<Integer, Class<? extends Fluid>> predicate, List<ITextComponent> tooltips);


    /*
     * -----
     * biome
     * -----
     */

    /** Must match the given biome */
    IAgriGrowCondition biome(IntPredicate strength, Biome biome, ITextComponent biomeName);

    /** Must match any of the given biomes */
    IAgriGrowCondition biome(IntPredicate strength, Function<Biome, ITextComponent> nameFunction, Biome... biomes);

    /** Must match any of the given biomes */
    IAgriGrowCondition biome(IntPredicate strength, Collection<Biome> biomes, Function<Biome, ITextComponent> nameFunction);

    /** Must match the predicate */
    IAgriGrowCondition biome(BiPredicate<Integer, Biome> predicate, List<ITextComponent> tooltips);

    /** Must match the given biome category */
    IAgriGrowCondition biomeFromCategory(IntPredicate strength, Biome.Category category, ITextComponent categoryName);

    /** Must match any of the given biome categories */
    IAgriGrowCondition biomeFromCategories(IntPredicate strength, Function<Biome.Category, ITextComponent> nameFunction, Biome.Category... categories);

    /** Must match any of the given biome categories */
    IAgriGrowCondition biomeFromCategories(IntPredicate strength, Collection<Biome.Category> categories, Function<Biome.Category, ITextComponent> nameFunction);

    /** Must match the predicate */
    IAgriGrowCondition biomeFromCategory(BiPredicate<Integer, Biome.Category> predicate, List<ITextComponent> tooltips);


    /*
     * -------
     * climate
     * -------
     */

    /** Must match the given climate */
    IAgriGrowCondition climate(IntPredicate strength, Biome.Climate climate, List<ITextComponent> tooltips);

    /** Must match any of the given climates */
    IAgriGrowCondition climate(IntPredicate strength, List<ITextComponent> tooltips, Biome.Climate... climates);

    /** Must match any of the given climates */
    IAgriGrowCondition climate(IntPredicate strength, Collection<Biome.Climate> climates, List<ITextComponent> tooltips);

    /** Must match the predicate */
    IAgriGrowCondition climate(BiPredicate<Integer, Biome.Climate> predicate, List<ITextComponent> tooltips);


    /*
     * ---------
     * dimension
     * ---------
     */
    /** Must match the given dimension */
    IAgriGrowCondition dimension(IntPredicate strength, ResourceLocation dimension, ITextComponent dimensionName);

    /** Must match any of the given dimensions */
    IAgriGrowCondition dimensions(IntPredicate strength, Function<ResourceLocation, ITextComponent> nameFunction, ResourceLocation... dimensions);

    /** Must match any of the given dimensions */
    IAgriGrowCondition dimensions(IntPredicate strength, Collection<ResourceLocation> dimensions, Function<ResourceLocation, ITextComponent> nameFunction);

    /** Must match the given dimension */
    IAgriGrowCondition dimensionFromKey(IntPredicate strength, RegistryKey<World> dimension, ITextComponent dimensionName);

    /** Must match any of the given dimensions */
    IAgriGrowCondition dimensionsFromKeys(IntPredicate strength, Function<RegistryKey<World>, ITextComponent> nameFunction, RegistryKey<World>... dimensions);

    /** Must match any of the given dimensions */
    IAgriGrowCondition dimensionsFromKeys(IntPredicate strength, Collection<RegistryKey<World>> dimensions, Function<RegistryKey<World>, ITextComponent> nameFunction);

    /** Must match the predicate */
    IAgriGrowCondition dimensionFromKey(BiPredicate<Integer, RegistryKey<World>> predicate, List<ITextComponent> tooltips);

    /** Must match the given dimension type */
    IAgriGrowCondition dimensionFromType(IntPredicate strength, DimensionType dimension, ITextComponent dimensionName);

    /** Must match any of the given dimension types */
    IAgriGrowCondition dimensionFromTypes(IntPredicate strength, Function<DimensionType, ITextComponent> nameFunction, DimensionType... dimensions);

    /** Must match any of the given dimension types */
    IAgriGrowCondition dimensionFromTypes(IntPredicate strength, Collection<DimensionType> dimensions, Function<DimensionType, ITextComponent> nameFunction);

    /** Must match the predicate */
    IAgriGrowCondition dimensionFromType(BiPredicate<Integer, DimensionType> predicate, List<ITextComponent> tooltips);


    /*
     * -----
     * weeds
     * -----
     */

    /** if any weed is needed for growth */
    IAgriGrowCondition withWeed(IntPredicate strength);

    /** if no weed is allowed for growth */
    IAgriGrowCondition withoutWeed(IntPredicate strength);

    /** if a specific weed is needed for growth */
    IAgriGrowCondition withWeed(IntPredicate strength, IAgriWeed weed);

    /** if a specific weed is not allowed for growth */
    IAgriGrowCondition withoutWeed(IntPredicate strength, IAgriWeed weed);

    /** matching the predicate */
    IAgriGrowCondition weed(BiPredicate<Integer, IAgriWeed> predicate, List<ITextComponent> tooltips);

    /** if a specific weed and growth stage is needed for growth */
    IAgriGrowCondition withWeed(IntPredicate strength, IAgriWeed weed, IAgriGrowthStage stage);

    /** if a specific weed and growth stage is not allowed for growth */
    IAgriGrowCondition withoutWeed(IntPredicate strength, IAgriWeed weed, IAgriGrowthStage stage);

    /** Must match the predicate */
    IAgriGrowCondition weed(TriPredicate<Integer, IAgriWeed, IAgriGrowthStage> predicate, List<ITextComponent> tooltips);


    /*
     * -----
     * time
     * -----
     */

    /** growth only allowed during the day */
    IAgriGrowCondition day(IntPredicate strength);

    /** growth only allowed during dusk */
    IAgriGrowCondition dusk(IntPredicate strength);

    /** growth only allowed during the night */
    IAgriGrowCondition night(IntPredicate strength);

    /** growth only allowed during dawn */
    IAgriGrowCondition dawn(IntPredicate strength);

    /** growth only allowed during the time of the day bounded by min and max (both inclusive) */
    IAgriGrowCondition timeAllowed(IntPredicate strength, long min, long max, List<ITextComponent> tooltips);

    /** growth not allowed during the time of the day bounded by min and max (both inclusive) */
    IAgriGrowCondition timeForbidden(IntPredicate strength, long min, long max, List<ITextComponent> tooltips);

    /** Must match the predicate */
    IAgriGrowCondition time(BiPredicate<Integer, Long> predicate, List<ITextComponent> tooltips);


    /*
     * ------
     * block below
     * ------
     */

    /** Must match the given block */
    IAgriGrowCondition blockBelow(IntPredicate strength, Block block);

    /** Must match any of the given blocks */
    IAgriGrowCondition blockBelow(IntPredicate strength, Block... blocks);

    /** Must match any of the given blocks */
    IAgriGrowCondition blockBelow(IntPredicate strength, Collection<Block> blocks);

    /** Must match the predicate */
    IAgriGrowCondition blockBelow(BiPredicate<Integer, Block> predicate, List<ITextComponent> tooltips);

    /** Must match the given state */
    IAgriGrowCondition stateBelow(IntPredicate strength, BlockState state);

    /** Must match any of the given states */
    IAgriGrowCondition stateBelow(IntPredicate strength, BlockState... states);

    /** Must match any of the given states */
    IAgriGrowCondition stateBelow(IntPredicate strength, Collection<BlockState> states);

    /** Must match the predicate */
    IAgriGrowCondition stateBelow(BiPredicate<Integer, BlockState> predicate, List<ITextComponent> tooltips);

    /** Must match the tag */
    IAgriGrowCondition tagBelow(IntPredicate strength, Tag<Block> tag);

    /** Must match any of the given tags */
    IAgriGrowCondition tagBelow(IntPredicate strength, Tag<Block>... tags);

    /** Must match any of the given tags */
    IAgriGrowCondition tagBelow(IntPredicate strength, Collection<Tag<Block>> tags);

    /** Must be instance of the given class */
    IAgriGrowCondition classBelow(IntPredicate strength, Class<Block> clazz, List<ITextComponent> tooltips);

    /** Must be instance of any of the given classes */
    IAgriGrowCondition classBelow(IntPredicate strength, List<ITextComponent> tooltips, Class<Block>... classes);

    /** Must be instance of any of the given classes */
    IAgriGrowCondition classBelow(IntPredicate strength, Collection<Class<Block>> classes, List<ITextComponent> tooltips);

    /** Must match the predicate */
    IAgriGrowCondition classBelow(BiPredicate<Integer, Class<? extends Block>> predicate, List<ITextComponent> tooltips);


    /*
     * -------------
     * blocks nearby
     * -------------
     */

    /** Must match amount of the given block in the range bound by the offsets */
    IAgriGrowCondition blocksNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, Block block);

    /** Must match amount of any of the given blocks in the range bound by the offsets */
    IAgriGrowCondition blocksNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, Block... blocks);

    /** Must match amount of any of the given blocks in the range bound by the offsets */
    IAgriGrowCondition blocksNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<Block> blocks);

    /** Must match amount of the predicate in the range bound by the offsets*/
    IAgriGrowCondition blocksNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, Predicate<Block> predicate, List<ITextComponent> tooltips);

    /** Must match amount of the given state in the range bound by the offsets */
    IAgriGrowCondition statesNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, BlockState state);

    /** Must match amount of any of the given states in the range bound by the offsets */
    IAgriGrowCondition statesNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, BlockState... states);

    /** Must match amount of any of the given states in the range bound by the offsets */
    IAgriGrowCondition statesNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<BlockState> states);

    /** Must match amount of the predicate in the range bound by the offsets*/
    IAgriGrowCondition statesNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, Predicate<BlockState> predicate, List<ITextComponent> tooltips);

    /** Must match amount of the tag in the range bound by the offsets */
    IAgriGrowCondition tagsNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, Tag<Block> tag);

    /** Must match amount of any of the given tags in the range bound by the offsets */
    IAgriGrowCondition tagsNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, Tag<Block>... tags);

    /** Must match amount of any of the given tags in the range bound by the offsets */
    IAgriGrowCondition tagsNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<Tag<Block>> tags);

    /** Amount must be instance of the given class in the range bound by the offsets */
    IAgriGrowCondition classNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset,
                                   Class<Block> clazz, List<ITextComponent> tooltips);

    /** Amount must be instance of any of the given classes in the range bound by the offsets */
    IAgriGrowCondition classNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset,
                                   List<ITextComponent> tooltips, Class<Block>... classes);

    /** Amount must be instance of any of the given classes in the range bound by the offsets */
    IAgriGrowCondition classNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset,
                                   Collection<Class<Block>> classes, List<ITextComponent> tooltips);

    /** Must match amount of the predicate in the range bound by the offsets*/
    IAgriGrowCondition classNearby(IntPredicate strength, int amount, BlockPos minOffset, BlockPos maxOffset,
                                   Predicate<Class<? extends Block>> predicate, List<ITextComponent> tooltips);

    /** Must match amount between min and max (both inclusive) of the given block in the range bound by the offsets */
    IAgriGrowCondition blocksNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Block block);

    /** Must match amount between min and max (both inclusive) of any of the given blocks in the range bound by the offsets */
    IAgriGrowCondition blocksNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Block... blocks);

    /** Must match amount between min and max (both inclusive) of any of the given blocks in the range bound by the offsets */
    IAgriGrowCondition blocksNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<Block> blocks);

    /** Must match amount between min and max (both inclusive) of the predicate in the range bound by the offsets*/
    IAgriGrowCondition blocksNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset,
                                    Predicate<Block> predicate, List<ITextComponent> tooltips);

    /** Must match amount between min and max (both inclusive) of the given state in the range bound by the offsets */
    IAgriGrowCondition statesNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, BlockState state);

    /** Must match amount between min and max (both inclusive) of any of the given states in the range bound by the offsets */
    IAgriGrowCondition statesNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, BlockState... states);

    /** Must match amount between min and max (both inclusive) of any of the given states in the range bound by the offsets */
    IAgriGrowCondition statesNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<BlockState> states);

    /** Must match amount between min and max (both inclusive) of the predicate in the range bound by the offsets*/
    IAgriGrowCondition statesNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset,
                                    Predicate<BlockState> predicate, List<ITextComponent> tooltips);

    /** Must match amount between min and max (both inclusive) of the tag in the range bound by the offsets */
    IAgriGrowCondition tagsNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Tag<Block> tag);

    /** Must match amount between min and max (both inclusive) of any of the given tags in the range bound by the offsets */
    IAgriGrowCondition tagsNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Tag<Block>... tags);

    /** Must match amount between min and max (both inclusive) of any of the given tags in the range bound by the offsets */
    IAgriGrowCondition tagsNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<Tag<Block>> tags);

    /** Amount between min and max (both inclusive) must be instance of the given class in the range bound by the offsets */
    IAgriGrowCondition classNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset,
                                   Class<Block> clazz, List<ITextComponent> tooltips);

    /** Amount between min and max (both inclusive) must be instance of any of the given classes in the range bound by the offsets */
    IAgriGrowCondition classNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset,
                                   List<ITextComponent> tooltips, Class<Block>... classes);

    /** Amount between min and max (both inclusive) must be instance of any of the given classes in the range bound by the offsets */
    IAgriGrowCondition classNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset,
                                   Collection<Class<Block>> classes, List<ITextComponent> tooltips);

    /** Must match amount between min and max (both inclusive) of the predicate in the range bound by the offsets*/
    IAgriGrowCondition classNearby(IntPredicate strength, int min, int max, BlockPos minOffset, BlockPos maxOffset,
                                   Predicate<Class<? extends Block>> predicate, List<ITextComponent> tooltips);


    /*
     * -------------
     * entity nearby
     * -------------
     */

    /** An entity of the type must by present within the range */
    IAgriGrowCondition entityNearby(IntPredicate strength, EntityType<?> type, double range);

    /** An entity instance of the class must by present within the range */
    IAgriGrowCondition entityNearby(IntPredicate strength, Class<Entity> clazz, double range, ITextComponent entityName);

    /** An entity matching the predicate must by present within the range */
    IAgriGrowCondition entityNearby(BiPredicate<Integer, Entity> predicate, double range, List<ITextComponent> tooltips);

    /** Amount of entities of the type must by present within the range */
    IAgriGrowCondition entitiesNearby(IntPredicate strength, EntityType<?> type, double range, int amount);

    /** Amount of entity instances of the class must by present within the range */
    IAgriGrowCondition entitiesNearby(IntPredicate strength, Class<Entity> clazz, double range, int amount, ITextComponent entityName);

    /** Amount of entities matching the predicate must by present within the range */
    IAgriGrowCondition entitiesNearby(BiPredicate<Integer, Entity> predicate, double range, int amount, List<ITextComponent> tooltips);

    /** Amount of entities bounded by min and max (both inclusive) of the type must by present within the range */
    IAgriGrowCondition entitiesNearby(IntPredicate strength, EntityType<?> type, double range, int min, int max);

    /** Amount of entity instances bounded by min and max (both inclusive) of the class must by present within the range */
    IAgriGrowCondition entitiesNearby(IntPredicate strength, Class<Entity> clazz, double range, int min, int max, ITextComponent entityName);

    /** Amount of entities bounded by min and max (both inclusive) matching the predicate must by present within the range */
    IAgriGrowCondition entitiesNearby(BiPredicate<Integer, Entity> predicate, double range, int min, int max, List<ITextComponent> tooltips);


    /*
     * ----
     * rain
     * ----
     */

    /** Must not be raining */
    IAgriGrowCondition noRain(IntPredicate strength);


    /** Must be raining */
    IAgriGrowCondition withRain(IntPredicate strength);


    /*
     * ----
     * snow
     * ----
     */

    /** Must not be snowing */
    IAgriGrowCondition noSnow(IntPredicate strength);


    /** Must be snowing */
    IAgriGrowCondition withSnow(IntPredicate strength);

    /*
     * -------
     * seasons
     * -------
     */

    /** Only during the given season */
    IAgriGrowCondition inSeason(IntPredicate strength, AgriSeason season);

    /** Only during the given seasons */
    IAgriGrowCondition inSeasons(IntPredicate strength, AgriSeason... seasons);

    /** Only during the given seasons */
    IAgriGrowCondition inSeasons(IntPredicate strength, Collection<AgriSeason> seasons);

    /** Not during the given season */
    IAgriGrowCondition notInSeason(IntPredicate strength, AgriSeason season);

    /** Not during the given seasons */
    IAgriGrowCondition notInSeasons(IntPredicate strength, AgriSeason... seasons);

    /** Not during the given seasons */
    IAgriGrowCondition notInSeasons(IntPredicate strength, Collection<AgriSeason> seasons);

    /** Only during seasons matching the predicate */
    IAgriGrowCondition season(BiPredicate<Integer, AgriSeason> predicate, List<ITextComponent> tooltips);

    /*
     * ----------
     * structures
     * ----------
     */

    /** Must be in a village */
    IAgriGrowCondition inVillage(IntPredicate strength);

    /** Must be in a pillager outpost */
    IAgriGrowCondition inPillagerOutpost(IntPredicate strength);

    /** Must be in a mineshaft */
    IAgriGrowCondition inMineshaft(IntPredicate strength);

    /** Must be in a mansion */
    IAgriGrowCondition inMansion(IntPredicate strength);

    /** Must be in a pyramid (jungle or desert) */
    IAgriGrowCondition inPyramid(IntPredicate strength);

    /** Must be in a jungle pyramid */
    IAgriGrowCondition inJunglePyramid(IntPredicate strength);

    /** Must be in a desert pyramid */
    IAgriGrowCondition inDesertPyramid(IntPredicate strength);

    /** Must be in an igloo */
    IAgriGrowCondition inIgloo(IntPredicate strength);

    /** Must be in a ruined portal */
    IAgriGrowCondition inRuinedPortal(IntPredicate strength);

    /** Must be in a shipwreck */
    IAgriGrowCondition inShipwreck(IntPredicate strength);

    /** Must be in a swamp hut */
    IAgriGrowCondition inSwampHut(IntPredicate strength);

    /** Must be in a stronghold */
    IAgriGrowCondition inStronghold(IntPredicate strength);

    /** Must be in a monument */
    IAgriGrowCondition inMonument(IntPredicate strength);

    /** Must be in an ocean ruin */
    IAgriGrowCondition inOceanRuin(IntPredicate strength);

    /** Must be in a fortress */
    IAgriGrowCondition inFortress(IntPredicate strength);

    /** Must be in an end city */
    IAgriGrowCondition inEndCity(IntPredicate strength);

    /** Must be in a buried treasure */
    IAgriGrowCondition inBuriedTreasure(IntPredicate strength);

    /** Must be in a nether fossil */
    IAgriGrowCondition inNetherFossil(IntPredicate strength);

    /** Must be in a bastion remnant */
    IAgriGrowCondition inBastionRemnant(IntPredicate strength);

    /** Must be in a village */
    IAgriGrowCondition notInVillage(IntPredicate strength);

    /** Must be in a pillager outpost */
    IAgriGrowCondition notInPillagerOutpost(IntPredicate strength);

    /** Must be in a mineshaft */
    IAgriGrowCondition notInMineshaft(IntPredicate strength);

    /** Must be in a mansion */
    IAgriGrowCondition notInMansion(IntPredicate strength);

    /** Must be in a pyramid (jungle or desert) */
    IAgriGrowCondition notInPyramid(IntPredicate strength);

    /** Must be in a jungle pyramid */
    IAgriGrowCondition notInJunglePyramid(IntPredicate strength);

    /** Must be in a desert pyramid */
    IAgriGrowCondition notInDesertPyramid(IntPredicate strength);

    /** Must be in an igloo */
    IAgriGrowCondition notInIgloo(IntPredicate strength);

    /** Must be in a ruined portal */
    IAgriGrowCondition notInRuinedPortal(IntPredicate strength);

    /** Must be in a shipwreck */
    IAgriGrowCondition notInShipwreck(IntPredicate strength);

    /** Must be in a swamp hut */
    IAgriGrowCondition notInSwampHut(IntPredicate strength);

    /** Must be in a stronghold */
    IAgriGrowCondition notInStronghold(IntPredicate strength);

    /** Must be in a monument */
    IAgriGrowCondition notInMonument(IntPredicate strength);

    /** Must be in an ocean ruin */
    IAgriGrowCondition notInOceanRuin(IntPredicate strength);

    /** Must be in a fortress */
    IAgriGrowCondition notInFortress(IntPredicate strength);

    /** Must be in an end city */
    IAgriGrowCondition notInEndCity(IntPredicate strength);

    /** Must be in a buried treasure */
    IAgriGrowCondition notInBuriedTreasure(IntPredicate strength);

    /** Must be in a nether fossil */
    IAgriGrowCondition notInNetherFossil(IntPredicate strength);

    /** Must be in a bastion remnant */
    IAgriGrowCondition notInBastionRemnant(IntPredicate strength);

    /** Must not be in the given structure */
    IAgriGrowCondition inStructure(IntPredicate strength, IForgeStructure structure, ITextComponent structureName);

    /** Must be in the given structure */
    IAgriGrowCondition notInStructure(IntPredicate strength, IForgeStructure structure, ITextComponent structureName);

    /** Must match the predicate */
    IAgriGrowCondition structure(BiPredicate<Integer, IForgeStructure> predicate, List<ITextComponent> tooltips);
}
