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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.extensions.IForgeStructure;

import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

/**
 * Class to obtain default AgriCraft implementations of the most common IGrowConditions.
 * Its instance can be retrieved via AgriApi.getDefaultGrowConditionFactory
 *
 * The strength parameter defines the strength stat level at which a plant will ignore this grow condition
 * if 0 is passed in, the condition will always be ignored (this makes no sense and should never be done)
 * if 11 or higher is passed in, the condition will never be ignored
 */
public interface IDefaultGrowConditionFactory {
    /*
     * ----
     * soil
     * ----
     */

    /** Must match the given soil */
    IAgriGrowCondition soil(int strength, IAgriSoil soil);

    /** Must match any of the given soils */
    IAgriGrowCondition soil(int strength, IAgriSoil... soils);

    /** Must match any of the given soils */
    IAgriGrowCondition soil(int strength, Collection<IAgriSoil> soils);


    /*
     * -----
     * light
     * -----
     */

    /** light value must be between the min and max (both inclusive) */
    IAgriGrowCondition light(int strength, int min, int max);

    /** light value must be the given value */
    IAgriGrowCondition light(int strength, int value);

    /** Must match the predicate */
    IAgriGrowCondition light(int strength, IntPredicate predicate);


    /*
     * --------
     * redstone
     * --------
     */

    /** redstone signal must be between the min and max (both inclusive) */
    IAgriGrowCondition redstone(int strength, int min, int max);

    /** redstone signal must be the given value */
    IAgriGrowCondition redstone(int strength, int value);

    /** Must match the predicate */
    IAgriGrowCondition redstone(int strength, IntPredicate predicate);


    /*
     * ------
     * liquid
     * ------
     */

    /** Must match the given fluid */
    IAgriGrowCondition liquidFromFluid(int strength, Fluid fluid);

    /** Must match any of the given fluids */
    IAgriGrowCondition liquidFromFluid(int strength, Fluid... fluids);

    /** Must match any of the given fluids */
    IAgriGrowCondition liquidFromFluid(int strength, Collection<Fluid> fluids);

    /** Must match the predicate */
    IAgriGrowCondition liquidFromFluid(int strength, Predicate<Fluid> predicate);

    /** Must match the given fluid state */
    IAgriGrowCondition liquidFromState(int strength, FluidState state);

    /** Must match any of the given fluid states */
    IAgriGrowCondition liquidFromState(int strength, FluidState... states);

    /** Must match any of the given fluid states */
    IAgriGrowCondition liquidFromState(int strength, Collection<FluidState> states);

    /** Must match the predicate */
    IAgriGrowCondition liquidFromState(int strength, Predicate<FluidState> predicate);

    /** Must match the tag */
    IAgriGrowCondition liquidFromTag(int strength, Tag<Fluid> fluid);

    /** Must match any of the given tags */
    IAgriGrowCondition liquidFromTag(int strength, Tag<Fluid>... fluids);

    /** Must match any of the given tags */
    IAgriGrowCondition liquidFromTag(int strength, Collection<Tag<Fluid>> fluids);

    /** Must be instance of the given class */
    IAgriGrowCondition liquidFromClass(int strength, Class<Fluid> fluid);

    /** Must be instance of any of the given classes */
    IAgriGrowCondition liquidFromClass(int strength, Class<Fluid>... fluids);

    /** Must be instance of any of the given classes */
    IAgriGrowCondition liquidFromClass(int strength, Collection<Class<Fluid>> fluids);

    /** Must match the predicate */
    IAgriGrowCondition liquidFromClass(int strength, Predicate<Class<? extends Fluid>> predicate);


    /*
     * -----
     * biome
     * -----
     */

    /** Must match the given biome */
    IAgriGrowCondition biome(int strength, Biome biome);

    /** Must match any of the given biomes */
    IAgriGrowCondition biome(int strength, Biome... biomes);

    /** Must match any of the given biomes */
    IAgriGrowCondition biome(int strength, Collection<Biome> biomes);

    /** Must match the predicate */
    IAgriGrowCondition biome(int strength, Predicate<Biome> predicate);

    /** Must match the given biome category */
    IAgriGrowCondition biomeFromCategory(int strength, Biome.Category category);

    /** Must match any of the given biome categories */
    IAgriGrowCondition biomeFromCategories(int strength, Biome.Category... categories);

    /** Must match any of the given biome categories */
    IAgriGrowCondition biomeFromCategories(int strength, Collection<Biome.Category> categories);

    /** Must match the predicate */
    IAgriGrowCondition biomeFromCategory(int strength, Predicate<Biome.Category> predicate);


    /*
     * -------
     * climate
     * -------
     */

    /** Must match the given climate */
    IAgriGrowCondition climate(int strength, Biome.Climate climate);

    /** Must match any of the given climates */
    IAgriGrowCondition climate(int strength, Biome.Climate... climates);

    /** Must match any of the given climates */
    IAgriGrowCondition climate(int strength, Collection<Biome.Climate> climates);

    /** Must match the predicate */
    IAgriGrowCondition climate(int strength, Predicate<Biome.Climate> predicate);


    /*
     * ---------
     * dimension
     * ---------
     */

    /** Must match the given dimension */
    IAgriGrowCondition dimensionFromKey(int strength, RegistryKey<World> dimension);

    /** Must match any of the given dimensions */
    IAgriGrowCondition dimensionFromKeys(int strength, RegistryKey<World>... dimensions);

    /** Must match any of the given dimensions */
    IAgriGrowCondition dimensionFromKeys(int strength, Collection<RegistryKey<World>> dimensions);

    /** Must match the predicate */
    IAgriGrowCondition dimensionFromKey(int strength, Predicate<RegistryKey<World>> predicate);

    /** Must match the given dimension type */
    IAgriGrowCondition dimensionFromType(int strength, DimensionType dimension);

    /** Must match any of the given dimension types */
    IAgriGrowCondition dimensionFromTypes(int strength, DimensionType... dimensions);

    /** Must match any of the given dimension types */
    IAgriGrowCondition dimensionFromTypes(int strength, Collection<DimensionType> dimensions);

    /** Must match the predicate */
    IAgriGrowCondition dimensionFromType(int strength, Predicate<DimensionType> predicate);


    /*
     * -----
     * weeds
     * -----
     */

    /** if any weed is needed for growth */
    IAgriGrowCondition withWeed(int strength);

    /** if no weed is allowed for growth */
    IAgriGrowCondition withoutWeed(int strength);

    /** if a specific weed is needed for growth */
    IAgriGrowCondition withWeed(int strength, IAgriWeed weed);

    /** if a specific weed is not allowed for growth */
    IAgriGrowCondition withoutWeed(int strength, IAgriWeed weed);

    /** matching the predicate */
    IAgriGrowCondition weed(int strength, Predicate<IAgriWeed> predicate);

    /** if a specific weed and growth stage is needed for growth */
    IAgriGrowCondition withWeed(int strength, IAgriWeed weed, IAgriGrowthStage stage);

    /** if a specific weed and growth stage is not allowed for growth */
    IAgriGrowCondition withoutWeed(int strength, IAgriWeed weed, IAgriGrowthStage stage);

    /** Must match the predicate */
    IAgriGrowCondition weed(int strength, BiPredicate<IAgriWeed, IAgriGrowthStage> predicate);


    /*
     * -----
     * time
     * -----
     */

    /** growth only allowed during the time of the day bounded by min and max (both inclusive) */
    IAgriGrowCondition timeAllowed(int strength, long min, long max);

    /** growth not allowed during the time of the day bounded by min and max (both inclusive) */
    IAgriGrowCondition timeForbidden(int strength, long min, long max);

    /** Must match the predicate */
    IAgriGrowCondition time(int strength, LongPredicate predicate);


    /*
     * ------
     * block below
     * ------
     */

    /** Must match the given block */
    IAgriGrowCondition blockBelow(int strength, Block block);

    /** Must match any of the given blocks */
    IAgriGrowCondition blockBelow(int strength, Block... blocks);

    /** Must match any of the given blocks */
    IAgriGrowCondition blockBelow(int strength, Collection<Block> blocks);

    /** Must match the predicate */
    IAgriGrowCondition blockBelow(int strength, Predicate<Block> predicate);

    /** Must match the given state */
    IAgriGrowCondition stateBelow(int strength, BlockState state);

    /** Must match any of the given states */
    IAgriGrowCondition stateBelow(int strength, BlockState... states);

    /** Must match any of the given states */
    IAgriGrowCondition stateBelow(int strength, Collection<BlockState> states);

    /** Must match the predicate */
    IAgriGrowCondition stateBelow(int strength, Predicate<BlockState> predicate);

    /** Must match the tag */
    IAgriGrowCondition tagBelow(int strength, Tag<Block> tag);

    /** Must match any of the given tags */
    IAgriGrowCondition tagBelow(int strength, Tag<Block>... tags);

    /** Must match any of the given tags */
    IAgriGrowCondition tagBelow(int strength, Collection<Tag<Block>> tags);

    /** Must be instance of the given class */
    IAgriGrowCondition classBelow(int strength, Class<Block> clazz);

    /** Must be instance of any of the given classes */
    IAgriGrowCondition classBelow(int strength, Class<Block>... classes);

    /** Must be instance of any of the given classes */
    IAgriGrowCondition classBelow(int strength, Collection<Class<Block>> classes);

    /** Must match the predicate */
    IAgriGrowCondition classBelow(int strength, Predicate<Class<? extends Block>> predicate);


    /*
     * -------------
     * blocks nearby
     * -------------
     */

    /** Must match amount of the given block in the range bound by the offsets */
    IAgriGrowCondition blocksNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Block block);

    /** Must match amount of any of the given blocks in the range bound by the offsets */
    IAgriGrowCondition blocksNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Block... blocks);

    /** Must match amount of any of the given blocks in the range bound by the offsets */
    IAgriGrowCondition blocksNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<Block> blocks);

    /** Must match amount of the predicate in the range bound by the offsets*/
    IAgriGrowCondition blocksNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Predicate<Block> predicate);

    /** Must match amount of the given state in the range bound by the offsets */
    IAgriGrowCondition statesNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, BlockState state);

    /** Must match amount of any of the given states in the range bound by the offsets */
    IAgriGrowCondition statesNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, BlockState... states);

    /** Must match amount of any of the given states in the range bound by the offsets */
    IAgriGrowCondition statesNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<BlockState> states);

    /** Must match amount of the predicate in the range bound by the offsets*/
    IAgriGrowCondition statesNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Predicate<BlockState> predicate);

    /** Must match amount of the tag in the range bound by the offsets */
    IAgriGrowCondition tagsNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Tag<Block> tag);

    /** Must match amount of any of the given tags in the range bound by the offsets */
    IAgriGrowCondition tagsNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Tag<Block>... tags);

    /** Must match amount of any of the given tags in the range bound by the offsets */
    IAgriGrowCondition tagsNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<Tag<Block>> tags);

    /** Amount must be instance of the given class in the range bound by the offsets */
    IAgriGrowCondition classNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Class<Block> clazz);

    /** Amount must be instance of any of the given classes in the range bound by the offsets */
    IAgriGrowCondition classNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Class<Block>... classes);

    /** Amount must be instance of any of the given classes in the range bound by the offsets */
    IAgriGrowCondition classNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<Class<Block>> classes);

    /** Must match amount of the predicate in the range bound by the offsets*/
    IAgriGrowCondition classNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Predicate<Class<? extends Block>> predicate);

    /** Must match amount between min and max (both inclusive) of the given block in the range bound by the offsets */
    IAgriGrowCondition blocksNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Block block);

    /** Must match amount between min and max (both inclusive) of any of the given blocks in the range bound by the offsets */
    IAgriGrowCondition blocksNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Block... blocks);

    /** Must match amount between min and max (both inclusive) of any of the given blocks in the range bound by the offsets */
    IAgriGrowCondition blocksNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<Block> blocks);

    /** Must match amount between min and max (both inclusive) of the predicate in the range bound by the offsets*/
    IAgriGrowCondition blocksNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Predicate<Block> predicate);

    /** Must match amount between min and max (both inclusive) of the given state in the range bound by the offsets */
    IAgriGrowCondition statesNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, BlockState state);

    /** Must match amount between min and max (both inclusive) of any of the given states in the range bound by the offsets */
    IAgriGrowCondition statesNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, BlockState... states);

    /** Must match amount between min and max (both inclusive) of any of the given states in the range bound by the offsets */
    IAgriGrowCondition statesNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<BlockState> states);

    /** Must match amount between min and max (both inclusive) of the predicate in the range bound by the offsets*/
    IAgriGrowCondition statesNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Predicate<BlockState> predicate);

    /** Must match amount between min and max (both inclusive) of the tag in the range bound by the offsets */
    IAgriGrowCondition tagsNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Tag<Block> tag);

    /** Must match amount between min and max (both inclusive) of any of the given tags in the range bound by the offsets */
    IAgriGrowCondition tagsNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Tag<Block>... tags);

    /** Must match amount between min and max (both inclusive) of any of the given tags in the range bound by the offsets */
    IAgriGrowCondition tagsNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<Tag<Block>> tags);

    /** Amount between min and max (both inclusive) must be instance of the given class in the range bound by the offsets */
    IAgriGrowCondition classNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Class<Block> clazz);

    /** Amount between min and max (both inclusive) must be instance of any of the given classes in the range bound by the offsets */
    IAgriGrowCondition classNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Class<Block>... classes);

    /** Amount between min and max (both inclusive) must be instance of any of the given classes in the range bound by the offsets */
    IAgriGrowCondition classNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<Class<Block>> classes);

    /** Must match amount between min and max (both inclusive) of the predicate in the range bound by the offsets*/
    IAgriGrowCondition classNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Predicate<Class<? extends Block>> predicate);


    /*
     * -------------
     * entity nearby
     * -------------
     */

    /** An entity of the type must by present within the range */
    IAgriGrowCondition entityNearby(int strength, EntityType<?> type, double range);

    /** An entity instance of the class must by present within the range */
    IAgriGrowCondition entityNearby(int strength, Class<Entity> clazz, double range);

    /** An entity matching the predicate must by present within the range */
    IAgriGrowCondition entityNearby(int strength, Predicate<Entity> predicate, double range);

    /** Amount of entities of the type must by present within the range */
    IAgriGrowCondition entitiesNearby(int strength, EntityType<?> type, double range, int amount);

    /** Amount of entity instances of the class must by present within the range */
    IAgriGrowCondition entitiesNearby(int strength, Class<Entity> clazz, double range, int amount);

    /** Amount of entities matching the predicate must by present within the range */
    IAgriGrowCondition entitiesNearby(int strength, Predicate<Entity> predicate, double range, int amount);

    /** Amount of entities bounded by min and max (both inclusive) of the type must by present within the range */
    IAgriGrowCondition entitiesNearby(int strength, EntityType<?> type, double range, int min, int max);

    /** Amount of entity instances bounded by min and max (both inclusive) of the class must by present within the range */
    IAgriGrowCondition entitiesNearby(int strength, Class<Entity> clazz, double range, int min, int max);

    /** Amount of entities bounded by min and max (both inclusive) matching the predicate must by present within the range */
    IAgriGrowCondition entitiesNearby(int strength, Predicate<Entity> predicate, double range, int min, int max);


    /*
     * ----
     * rain
     * ----
     */

    /** Must not be raining */
    IAgriGrowCondition noRain(int strength);


    /** Must be raining */
    IAgriGrowCondition withRain(int strength);


    /*
     * ----
     * snow
     * ----
     */

    /** Must not be snowing */
    IAgriGrowCondition noSnow(int strength);


    /** Must be snowing */
    IAgriGrowCondition withSnow(int strength);

    /*
     * -------
     * seasons
     * -------
     */

    /** Only during the given season */
    IAgriGrowCondition inSeason(int strength, AgriSeason season);

    /** Only during the given seasons */
    IAgriGrowCondition inSeasons(int strength, AgriSeason... seasons);

    /** Only during the given seasons */
    IAgriGrowCondition inSeasons(int strength, Collection<AgriSeason> seasons);

    /** Not during the given season */
    IAgriGrowCondition notInSeason(int strength, AgriSeason season);

    /** Not during the given seasons */
    IAgriGrowCondition notInSeasons(int strength, AgriSeason... seasons);

    /** Not during the given seasons */
    IAgriGrowCondition notInSeasons(int strength, Collection<AgriSeason> seasons);

    /** Only during seasons matching the predicate */
    IAgriGrowCondition season(int strength, Predicate<AgriSeason> predicate);

    /*
     * ----------
     * structures
     * ----------
     */

    /** Must be in a village */
    IAgriGrowCondition inVillage(int strength);

    /** Must be in a pillager outpost */
    IAgriGrowCondition inPillagerOutpost(int strength);

    /** Must be in a mineshaft */
    IAgriGrowCondition inMineshaft(int strength);

    /** Must be in a mansion */
    IAgriGrowCondition inMansion(int strength);

    /** Must be in a pyramid (jungle or desert) */
    IAgriGrowCondition inPyramid(int strength);

    /** Must be in a jungle pyramid */
    IAgriGrowCondition inJunglePyramid(int strength);

    /** Must be in a desert pyramid */
    IAgriGrowCondition inDesertPyramid(int strength);

    /** Must be in an igloo */
    IAgriGrowCondition inIgloo(int strength);

    /** Must be in a ruined portal */
    IAgriGrowCondition inRuinedPortal(int strength);

    /** Must be in a shipwreck */
    IAgriGrowCondition inShipwreck(int strength);

    /** Must be in a swamp hut */
    IAgriGrowCondition inSwampHut(int strength);

    /** Must be in a stronghold */
    IAgriGrowCondition inStronghold(int strength);

    /** Must be in a monument */
    IAgriGrowCondition inMonument(int strength);

    /** Must be in an ocean ruin */
    IAgriGrowCondition inOceanRuin(int strength);

    /** Must be in a fortress */
    IAgriGrowCondition inFortress(int strength);

    /** Must be in an end city */
    IAgriGrowCondition inEndCity(int strength);

    /** Must be in a buried treasure */
    IAgriGrowCondition inBuriedTreasure(int strength);

    /** Must be in a nether fossil */
    IAgriGrowCondition inNetherFossil(int strength);

    /** Must be in a bastion remnant */
    IAgriGrowCondition inBastionRemnant(int strength);

    /** Must be in a village */
    IAgriGrowCondition notInVillage(int strength);

    /** Must be in a pillager outpost */
    IAgriGrowCondition notInPillagerOutpost(int strength);

    /** Must be in a mineshaft */
    IAgriGrowCondition notInMineshaft(int strength);

    /** Must be in a mansion */
    IAgriGrowCondition notInMansion(int strength);

    /** Must be in a pyramid (jungle or desert) */
    IAgriGrowCondition notInPyramid(int strength);

    /** Must be in a jungle pyramid */
    IAgriGrowCondition notInJunglePyramid(int strength);

    /** Must be in a desert pyramid */
    IAgriGrowCondition notInDesertPyramid(int strength);

    /** Must be in an igloo */
    IAgriGrowCondition notInIgloo(int strength);

    /** Must be in a ruined portal */
    IAgriGrowCondition notInRuinedPortal(int strength);

    /** Must be in a shipwreck */
    IAgriGrowCondition notInShipwreck(int strength);

    /** Must be in a swamp hut */
    IAgriGrowCondition notInSwampHut(int strength);

    /** Must be in a stronghold */
    IAgriGrowCondition notInStronghold(int strength);

    /** Must be in a monument */
    IAgriGrowCondition notInMonument(int strength);

    /** Must be in an ocean ruin */
    IAgriGrowCondition notInOceanRuin(int strength);

    /** Must be in a fortress */
    IAgriGrowCondition notInFortress(int strength);

    /** Must be in an end city */
    IAgriGrowCondition notInEndCity(int strength);

    /** Must be in a buried treasure */
    IAgriGrowCondition notInBuriedTreasure(int strength);

    /** Must be in a nether fossil */
    IAgriGrowCondition notInNetherFossil(int strength);

    /** Must be in a bastion remnant */
    IAgriGrowCondition notInBastionRemnant(int strength);

    /** Must not be in the given structure */
    IAgriGrowCondition inStructure(int strength, IForgeStructure structure);

    /** Must be in the given structure */
    IAgriGrowCondition notInStructure(int strength, IForgeStructure structure);

    /** Must match the predicate */
    IAgriGrowCondition structure(int strength, Predicate<IForgeStructure> predicate);
}
