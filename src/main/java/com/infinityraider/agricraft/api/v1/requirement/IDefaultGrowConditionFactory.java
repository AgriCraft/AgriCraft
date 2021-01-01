package com.infinityraider.agricraft.api.v1.requirement;

import com.infinityraider.agricraft.api.v1.plant.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoil;
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
 * The instance can be retrieved via AgriApi.getDefaultGrowConditionFactory
 */
public interface IDefaultGrowConditionFactory {
    /**
     * ----
     * soil
     * ----
     */

    /** Must match the given block */
    IGrowCondition soil(int strength, IAgriSoil soil);

    /** Must match any of the given blocks */
    IGrowCondition soil(int strength, IAgriSoil... soils);

    /** Must match any of the given blocks */
    IGrowCondition soil(int strength, Collection<IAgriSoil> soils);


    /**
     * -----
     * light
     * -----
     */

    /** light value must be between the min and max (both inclusive) */
    IGrowCondition light(int strength, int min, int max);

    /** light value must be the given value */
    IGrowCondition light(int strength, int value);

    /** Must match the predicate */
    IGrowCondition light(int strength, IntPredicate predicate);


    /**
     * --------
     * redstone
     * --------
     */

    /** redstone signal must be between the min and max (both inclusive) */
    IGrowCondition redstone(int strength, int min, int max);

    /** redstone signal must be the given value */
    IGrowCondition redstone(int strength, int value);

    /** Must match the predicate */
    IGrowCondition redstone(int strength, IntPredicate predicate);


    /**
     * ------
     * liquid
     * ------
     */

    /** Must match the given fluid */
    IGrowCondition liquidFromFluid(int strength, Fluid fluid);

    /** Must match any of the given fluids */
    IGrowCondition liquidFromFluid(int strength, Fluid... fluids);

    /** Must match any of the given fluids */
    IGrowCondition liquidFromFluid(int strength, Collection<Fluid> fluids);

    /** Must match the predicate */
    IGrowCondition liquidFromFluid(int strength, Predicate<Fluid> predicate);

    /** Must match the given fluid state */
    IGrowCondition liquidFromState(int strength, FluidState state);

    /** Must match any of the given fluid states */
    IGrowCondition liquidFromState(int strength, FluidState... states);

    /** Must match any of the given fluid states */
    IGrowCondition liquidFromState(int strength, Collection<FluidState> states);

    /** Must match the predicate */
    IGrowCondition liquidFromState(int strength, Predicate<FluidState> predicate);

    /** Must match the tag */
    IGrowCondition liquidFromTag(int strength, Tag<Fluid> fluid);

    /** Must match any of the given tags */
    IGrowCondition liquidFromTag(int strength, Tag<Fluid>... fluids);

    /** Must match any of the given tags */
    IGrowCondition liquidFromTag(int strength, Collection<Tag<Fluid>> fluids);

    /** Must be instance of the given class */
    IGrowCondition liquidFromClass(int strength, Class<Fluid> fluid);

    /** Must be instance of any of the given classes */
    IGrowCondition liquidFromClass(int strength, Class<Fluid>... fluids);

    /** Must be instance of any of the given classes */
    IGrowCondition liquidFromClass(int strength, Collection<Class<Fluid>> fluids);

    /** Must match the predicate */
    IGrowCondition liquidFromClass(int strength, Predicate<Class<? extends Fluid>> predicate);


    /**
     * -----
     * biome
     * -----
     */

    /** Must match the given biome */
    IGrowCondition biome(int strength, Biome biome);

    /** Must match any of the given biomes */
    IGrowCondition biome(int strength, Biome... biomes);

    /** Must match any of the given biomes */
    IGrowCondition biome(int strength, Collection<Biome> biomes);

    /** Must match the predicate */
    IGrowCondition biome(int strength, Predicate<Biome> predicate);

    /** Must match the given biome category */
    IGrowCondition biomeFromCategory(int strength, Biome.Category category);

    /** Must match any of the given biome categories */
    IGrowCondition biomeFromCategories(int strength, Biome.Category... categories);

    /** Must match any of the given biome categories */
    IGrowCondition biomeFromCategories(int strength, Collection<Biome.Category> categories);

    /** Must match the predicate */
    IGrowCondition biomeFromCategory(int strength, Predicate<Biome.Category> predicate);


    /**
     * -------
     * climate
     * -------
     */

    /** Must match the given climate */
    IGrowCondition climate(int strength, Biome.Climate climate);

    /** Must match any of the given climates */
    IGrowCondition climate(int strength, Biome.Climate... climates);

    /** Must match any of the given climates */
    IGrowCondition climate(int strength, Collection<Biome.Climate> climates);

    /** Must match the predicate */
    IGrowCondition climate(int strength, Predicate<Biome.Climate> predicate);


    /**
     * ---------
     * dimension
     * ---------
     */

    /** Must match the given dimension */
    IGrowCondition dimensionFromKey(int strength, RegistryKey<World> dimension);

    /** Must match any of the given dimensions */
    IGrowCondition dimensionFromKeys(int strength, RegistryKey<World>... dimensions);

    /** Must match any of the given dimensions */
    IGrowCondition dimensionFromKeys(int strength, Collection<RegistryKey<World>> dimensions);

    /** Must match the predicate */
    IGrowCondition dimensionFromKey(int strength, Predicate<RegistryKey<World>> predicate);

    /** Must match the given dimension type */
    IGrowCondition dimensionFromType(int strength, DimensionType dimension);

    /** Must match any of the given dimension types */
    IGrowCondition dimensionFromTypes(int strength, DimensionType... dimensions);

    /** Must match any of the given dimension types */
    IGrowCondition dimensionFromTypes(int strength, Collection<DimensionType> dimensions);

    /** Must match the predicate */
    IGrowCondition dimensionFromType(int strength, Predicate<DimensionType> predicate);


    /**
     * -----
     * weeds
     * -----
     */

    /** if any weed is needed for growth */
    IGrowCondition withWeed(int strength);

    /** if no weed is allowed for growth */
    IGrowCondition withoutWeed(int strength);

    /** if a specific weed is needed for growth */
    IGrowCondition withWeed(int strength, IAgriWeed weed);

    /** if a specific weed is not allowed for growth */
    IGrowCondition withoutWeed(int strength, IAgriWeed weed);

    /** matching the predicate */
    IGrowCondition weed(int strength, Predicate<IAgriWeed> predicate);

    /** if a specific weed and growth stage is needed for growth */
    IGrowCondition withWeed(int strength, IAgriWeed weed, IAgriGrowthStage stage);

    /** if a specific weed and growth stage is not allowed for growth */
    IGrowCondition withoutWeed(int strength, IAgriWeed weed, IAgriGrowthStage stage);

    /** Must match the predicate */
    IGrowCondition weed(int strength, BiPredicate<IAgriWeed, IAgriGrowthStage> predicate);


    /**
     * -----
     * time
     * -----
     */

    /** growth only allowed during the time of the day bounded by min and max (both inclusive) */
    IGrowCondition timeAllowed(int strength, long min, long max);

    /** growth not allowed during the time of the day bounded by min and max (both inclusive) */
    IGrowCondition timeForbidden(int strength, long min, long max);

    /** Must match the predicate */
    IGrowCondition time(int strength, LongPredicate predicate);


    /**
     * ------
     * block below
     * ------
     */

    /** Must match the given block */
    IGrowCondition blockBelow(int strength, Block block);

    /** Must match any of the given blocks */
    IGrowCondition blockBelow(int strength, Block... blocks);

    /** Must match any of the given blocks */
    IGrowCondition blockBelow(int strength, Collection<Block> blocks);

    /** Must match the predicate */
    IGrowCondition blockBelow(int strength, Predicate<Block> predicate);

    /** Must match the given state */
    IGrowCondition stateBelow(int strength, BlockState state);

    /** Must match any of the given states */
    IGrowCondition stateBelow(int strength, BlockState... states);

    /** Must match any of the given states */
    IGrowCondition stateBelow(int strength, Collection<BlockState> states);

    /** Must match the predicate */
    IGrowCondition stateBelow(int strength, Predicate<BlockState> predicate);

    /** Must match the tag */
    IGrowCondition tagBelow(int strength, Tag<Block> tag);

    /** Must match any of the given tags */
    IGrowCondition tagBelow(int strength, Tag<Block>... tags);

    /** Must match any of the given tags */
    IGrowCondition tagBelow(int strength, Collection<Tag<Block>> tags);

    /** Must be instance of the given class */
    IGrowCondition classBelow(int strength, Class<Block> clazz);

    /** Must be instance of any of the given classes */
    IGrowCondition classBelow(int strength, Class<Block>... classes);

    /** Must be instance of any of the given classes */
    IGrowCondition classBelow(int strength, Collection<Class<Block>> classes);

    /** Must match the predicate */
    IGrowCondition classBelow(int strength, Predicate<Class<? extends Block>> predicate);


    /**
     * -------------
     * blocks nearby
     * -------------
     */

    /** Must match amount of the given block in the range bound by the offsets */
    IGrowCondition blocksNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Block block);

    /** Must match amount of any of the given blocks in the range bound by the offsets */
    IGrowCondition blocksNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Block... blocks);

    /** Must match amount of any of the given blocks in the range bound by the offsets */
    IGrowCondition blocksNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<Block> blocks);

    /** Must match amount of the predicate in the range bound by the offsets*/
    IGrowCondition blocksNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Predicate<Block> predicate);

    /** Must match amount of the given state in the range bound by the offsets */
    IGrowCondition statesNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, BlockState state);

    /** Must match amount of any of the given states in the range bound by the offsets */
    IGrowCondition statesNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, BlockState... states);

    /** Must match amount of any of the given states in the range bound by the offsets */
    IGrowCondition statesNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<BlockState> states);

    /** Must match amount of the predicate in the range bound by the offsets*/
    IGrowCondition statesNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Predicate<BlockState> predicate);

    /** Must match amount of the tag in the range bound by the offsets */
    IGrowCondition tagsNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Tag<Block> tag);

    /** Must match amount of any of the given tags in the range bound by the offsets */
    IGrowCondition tagsNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Tag<Block>... tags);

    /** Must match amount of any of the given tags in the range bound by the offsets */
    IGrowCondition tagsNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<Tag<Block>> tags);

    /** Amount must be instance of the given class in the range bound by the offsets */
    IGrowCondition classNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Class<Block> clazz);

    /** Amount must be instance of any of the given classes in the range bound by the offsets */
    IGrowCondition classNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Class<Block>... classes);

    /** Amount must be instance of any of the given classes in the range bound by the offsets */
    IGrowCondition classNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Collection<Class<Block>> classes);

    /** Must match amount of the predicate in the range bound by the offsets*/
    IGrowCondition classNearby(int strength, int amount, BlockPos minOffset, BlockPos maxOffset, Predicate<Class<? extends Block>> predicate);

    /** Must match amount between min and max (both inclusive) of the given block in the range bound by the offsets */
    IGrowCondition blocksNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Block block);

    /** Must match amount between min and max (both inclusive) of any of the given blocks in the range bound by the offsets */
    IGrowCondition blocksNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Block... blocks);

    /** Must match amount between min and max (both inclusive) of any of the given blocks in the range bound by the offsets */
    IGrowCondition blocksNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<Block> blocks);

    /** Must match amount between min and max (both inclusive) of the predicate in the range bound by the offsets*/
    IGrowCondition blocksNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Predicate<Block> predicate);

    /** Must match amount between min and max (both inclusive) of the given state in the range bound by the offsets */
    IGrowCondition statesNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, BlockState state);

    /** Must match amount between min and max (both inclusive) of any of the given states in the range bound by the offsets */
    IGrowCondition statesNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, BlockState... states);

    /** Must match amount between min and max (both inclusive) of any of the given states in the range bound by the offsets */
    IGrowCondition statesNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<BlockState> states);

    /** Must match amount between min and max (both inclusive) of the predicate in the range bound by the offsets*/
    IGrowCondition statesNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Predicate<BlockState> predicate);

    /** Must match amount between min and max (both inclusive) of the tag in the range bound by the offsets */
    IGrowCondition tagsNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Tag<Block> tag);

    /** Must match amount between min and max (both inclusive) of any of the given tags in the range bound by the offsets */
    IGrowCondition tagsNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Tag<Block>... tags);

    /** Must match amount between min and max (both inclusive) of any of the given tags in the range bound by the offsets */
    IGrowCondition tagsNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<Tag<Block>> tags);

    /** Amount between min and max (both inclusive) must be instance of the given class in the range bound by the offsets */
    IGrowCondition classNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Class<Block> clazz);

    /** Amount between min and max (both inclusive) must be instance of any of the given classes in the range bound by the offsets */
    IGrowCondition classNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Class<Block>... classes);

    /** Amount between min and max (both inclusive) must be instance of any of the given classes in the range bound by the offsets */
    IGrowCondition classNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Collection<Class<Block>> classes);

    /** Must match amount between min and max (both inclusive) of the predicate in the range bound by the offsets*/
    IGrowCondition classNearby(int strength, int min, int max, BlockPos minOffset, BlockPos maxOffset, Predicate<Class<? extends Block>> predicate);


    /**
     * -------------
     * entity nearby
     * -------------
     */

    /** An entity of the type must by present within the range */
    IGrowCondition entityNearby(int strength, EntityType<?> type, double range);

    /** An entity instance of the class must by present within the range */
    IGrowCondition entityNearby(int strength, Class<Entity> clazz, double range);

    /** An entity matching the predicate must by present within the range */
    IGrowCondition entityNearby(int strength, Predicate<Entity> predicate, double range);

    /** Amount of entities of the type must by present within the range */
    IGrowCondition entitiesNearby(int strength, EntityType<?> type, double range, int amount);

    /** Amount of entity instances of the class must by present within the range */
    IGrowCondition entitiesNearby(int strength, Class<Entity> clazz, double range, int amount);

    /** Amount of entities matching the predicate must by present within the range */
    IGrowCondition entitiesNearby(int strength, Predicate<Entity> predicate, double range, int amount);

    /** Amount of entities bounded by min and max (both inclusive) of the type must by present within the range */
    IGrowCondition entitiesNearby(int strength, EntityType<?> type, double range, int min, int max);

    /** Amount of entity instances bounded by min and max (both inclusive) of the class must by present within the range */
    IGrowCondition entitiesNearby(int strength, Class<Entity> clazz, double range, int min, int max);

    /** Amount of entities bounded by min and max (both inclusive) matching the predicate must by present within the range */
    IGrowCondition entitiesNearby(int strength, Predicate<Entity> predicate, double range, int min, int max);


    /**
     * ----
     * rain
     * ----
     */

    /** Must not be raining */
    IGrowCondition noRain(int strength);


    /** Must be raining */
    IGrowCondition withRain(int strength);


    /**
     * ----
     * snow
     * ----
     */

    /** Must not be snowing */
    IGrowCondition noSnow(int strength);


    /** Must be snowing */
    IGrowCondition withSnow(int strength);


    /**
     * ----------
     * structures
     * ----------
     */

    /** Must be in a village */
    IGrowCondition inVillage(int strength);

    /** Must be in a pillager outpost */
    IGrowCondition inPillagerOutpost(int strength);

    /** Must be in a mineshaft */
    IGrowCondition inMineshaft(int strength);

    /** Must be in a mansion */
    IGrowCondition inMansion(int strength);

    /** Must be in a pyramid (jungle or desert) */
    IGrowCondition inPyramid(int strength);

    /** Must be in a jungle pyramid */
    IGrowCondition inJunglePyramid(int strength);

    /** Must be in a desert pyramid */
    IGrowCondition inDesertPyramid(int strength);

    /** Must be in an igloo */
    IGrowCondition inIgloo(int strength);

    /** Must be in a ruined portal */
    IGrowCondition inRuinedPortal(int strength);

    /** Must be in a shipwreck */
    IGrowCondition inShipwreck(int strength);

    /** Must be in a swamp hut */
    IGrowCondition inSwampHut(int strength);

    /** Must be in a stronghold */
    IGrowCondition inStronghold(int strength);

    /** Must be in a monument */
    IGrowCondition inMonument(int strength);

    /** Must be in an ocean ruin */
    IGrowCondition inOceanRuin(int strength);

    /** Must be in a fortress */
    IGrowCondition inFortress(int strength);

    /** Must be in an end city */
    IGrowCondition inEndCity(int strength);

    /** Must be in a buried treasure */
    IGrowCondition inBuriedTreasure(int strength);

    /** Must be in a nether fossil */
    IGrowCondition inNetherFossil(int strength);

    /** Must be in a bastion remnant */
    IGrowCondition inBastionRemnant(int strength);

    /** Must be in a village */
    IGrowCondition notInVillage(int strength);

    /** Must be in a pillager outpost */
    IGrowCondition notInPillagerOutpost(int strength);

    /** Must be in a mineshaft */
    IGrowCondition notInMineshaft(int strength);

    /** Must be in a mansion */
    IGrowCondition notInMansion(int strength);

    /** Must be in a pyramid (jungle or desert) */
    IGrowCondition notInPyramid(int strength);

    /** Must be in a jungle pyramid */
    IGrowCondition notInJunglePyramid(int strength);

    /** Must be in a desert pyramid */
    IGrowCondition notInDesertPyramid(int strength);

    /** Must be in an igloo */
    IGrowCondition notInIgloo(int strength);

    /** Must be in a ruined portal */
    IGrowCondition notInRuinedPortal(int strength);

    /** Must be in a shipwreck */
    IGrowCondition notInShipwreck(int strength);

    /** Must be in a swamp hut */
    IGrowCondition notInSwampHut(int strength);

    /** Must be in a stronghold */
    IGrowCondition notInStronghold(int strength);

    /** Must be in a monument */
    IGrowCondition notInMonument(int strength);

    /** Must be in an ocean ruin */
    IGrowCondition notInOceanRuin(int strength);

    /** Must be in a fortress */
    IGrowCondition notInFortress(int strength);

    /** Must be in an end city */
    IGrowCondition notInEndCity(int strength);

    /** Must be in a buried treasure */
    IGrowCondition notInBuriedTreasure(int strength);

    /** Must be in a nether fossil */
    IGrowCondition notInNetherFossil(int strength);

    /** Must be in a bastion remnant */
    IGrowCondition notInBastionRemnant(int strength);

    /** Must not be in the given structure */
    IGrowCondition inStructure(int strength, IForgeStructure structure);

    /** Must be in the given structure */
    IGrowCondition notInStructure(int strength, IForgeStructure structure);

    /** Must match the predicate */
    IGrowCondition structure(int strength, Predicate<IForgeStructure> predicate);
}
