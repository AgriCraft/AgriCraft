package com.infinityraider.agricraft.impl.v1.requirement;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.api.v1.requirement.IDefaultGrowConditionFactory;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowCondition;
import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import com.infinityraider.infinitylib.utility.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.common.extensions.IForgeStructure;
import net.minecraftforge.common.util.TriPredicate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Factory extends FactoryAbstract {
    private static final Factory INSTANCE = new Factory();

    private static final BiFunction<World, BlockPos, Integer> LIGHT_FUNCTION = IWorldReader::getLight;

    private static final BiFunction<World, BlockPos, Integer> REDSTONE_FUNCTION = World::getRedstonePowerFromNeighbors;

    private static final BiFunction<World, BlockPos, Biome> BIOME_FUNCTION = World::getBiome;

    private static final Function<World, RegistryKey<World>> DIMENSION_KEY_FUNCTION = World::getDimensionKey;

    private static final Function<World, DimensionType> DIMENSION_TYPE_FUNCTION = World::getDimensionType;
    private static final Function<World, Long> TIME_FUNCTION = World::getDayTime;

    private static final BiFunction<World, BlockPos, Boolean> RAIN_FUNCTION = (world, pos) ->
            world.isRainingAt(pos) && world.getBiome(pos).getPrecipitation() == Biome.RainType.RAIN;

    private static final BiFunction<World, BlockPos, Boolean> SNOW_FUNCTION = (world, pos) ->
            world.isRainingAt(pos) && world.getBiome(pos).getPrecipitation() == Biome.RainType.SNOW;

    private static final BiFunction<World, BlockPos, AgriSeason> SEASON_FUNCTION = (world, pos) ->
            AgriApi.getSeasonLogic().getSeason(world, pos);

    private static final BiFunction<World, BlockPos, Optional<IAgriCrop>> CROP_FUNCTION = AgriApi::getCrop;

    private static final BiFunction<World, BlockPos, Stream<Structure<?>>> STRUCTURE_FUNCTION = (world, pos) ->
            world.getChunkAt(pos).getStructureReferences().entrySet().stream()
                    .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                    .map(Map.Entry::getKey);

    private static BiFunction<World, BlockPos, Stream<Entity>> entityFunction(double range) {
        return (world, pos) ->
                world.getEntitiesWithinAABBExcludingEntity(
                        null, new AxisAlignedBB(pos.add(-range, -range, -range), pos.add(range, range, range))).stream();
    }

    private static BiFunction<World, BlockPos, Stream<BlockState>> blockStateFunction(BlockPos min, BlockPos max) {
        return (world, pos) -> WorldHelper.streamPositions(pos.add(min), pos.add(max)).map(world::getBlockState);
    }

    private static final UnaryOperator<BlockPos> OFFSETTER_NONE = pos -> pos;
    private static final UnaryOperator<BlockPos> OFFSETTER_SOIL = BlockPos::down;

    private static final List<ITextComponent> TOOLTIP_NO_RAIN = ImmutableList.of(
            new TranslationTextComponent(AgriCraft.instance.getModId() + ".tooltip.growth_req.no_rain"));
    private static final List<ITextComponent> TOOLTIP_RAIN = ImmutableList.of(
            new TranslationTextComponent(AgriCraft.instance.getModId() + ".tooltip.growth_req.rain"));
    private static final List<ITextComponent> TOOLTIP_NO_SNOW = ImmutableList.of(
            new TranslationTextComponent(AgriCraft.instance.getModId() + ".tooltip.growth_req.no_snow"));
    private static final List<ITextComponent> TOOLTIP_SNOW = ImmutableList.of(
            new TranslationTextComponent(AgriCraft.instance.getModId() + ".tooltip.growth_req.snow"));

    public static IDefaultGrowConditionFactory getInstance() {
        return INSTANCE;
    }

    protected Factory() {}

    @Override
    protected IAgriGrowCondition statesInRange(RequirementType type, BiPredicate<Integer, BlockState> predicate, int min, int max,
                                               BlockPos minOffset, BlockPos maxOffset,
                                               List<ITextComponent> tooltips) {
        return new GrowConditionMulti<>(type, predicate, blockStateFunction(minOffset, maxOffset), min, max, OFFSETTER_NONE,
                tooltips, (maxOffset.getX() - minOffset.getX())*(maxOffset.getX() - minOffset.getX())*(maxOffset.getX() - minOffset.getX()),
                IAgriGrowCondition.CacheType.BLOCK_UPDATE, WorldHelper.streamPositions(minOffset, maxOffset).collect(Collectors.toSet()));
    }

    @Override
    public IAgriGrowCondition light(BiPredicate<Integer, Integer> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionSingle<>(RequirementType.LIGHT, predicate, LIGHT_FUNCTION, OFFSETTER_NONE,
                tooltips, 1, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public IAgriGrowCondition redstone(BiPredicate<Integer, Integer> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionSingle<>(RequirementType.REDSTONE, predicate, REDSTONE_FUNCTION, OFFSETTER_SOIL,
                tooltips, 1, IAgriGrowCondition.CacheType.BLOCK_UPDATE);
    }

    @Override
    public IAgriGrowCondition biome(BiPredicate<Integer, Biome> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionSingle<>(RequirementType.BIOME, predicate, BIOME_FUNCTION, OFFSETTER_NONE,
                tooltips, 1, IAgriGrowCondition.CacheType.FULL);
    }

    @Override
    public IAgriGrowCondition dimensionFromKey(BiPredicate<Integer, RegistryKey<World>> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionAmbient<>(RequirementType.DIMENSION, predicate, DIMENSION_KEY_FUNCTION,
                tooltips, IAgriGrowCondition.CacheType.FULL);
    }

    @Override
    public IAgriGrowCondition dimensionFromType(BiPredicate<Integer, DimensionType> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionAmbient<>(RequirementType.DIMENSION, predicate, DIMENSION_TYPE_FUNCTION,
                tooltips, IAgriGrowCondition.CacheType.FULL);
    }

    @Override
    public IAgriGrowCondition weed(TriPredicate<Integer, IAgriWeed, IAgriGrowthStage> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionSingle<>(RequirementType.WEEDS, (str, opt) -> opt.map(crop -> predicate.test(str, crop.getWeeds(), crop.getWeedGrowthStage())).orElse(false),
                CROP_FUNCTION, OFFSETTER_NONE, tooltips, 1, IAgriGrowCondition.CacheType.BLOCK_UPDATE);
    }

    @Override
    public IAgriGrowCondition time(BiPredicate<Integer, Long> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionAmbient<>(RequirementType.TIME, predicate, TIME_FUNCTION,
                tooltips, IAgriGrowCondition.CacheType.FULL);
    }

    @Override
    public IAgriGrowCondition entitiesNearby(BiPredicate<Integer, Entity> predicate, double range, int min, int max,
                                             List<ITextComponent> tooltips) {
        return new GrowConditionMulti<>(RequirementType.ENTITY, predicate, entityFunction(range), min, max, OFFSETTER_NONE,
                tooltips, 1, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public IAgriGrowCondition noRain(IntPredicate strength) {
        return new GrowConditionSingle<>(RequirementType.RAIN, (str, value) -> strength.test(str) && !value, RAIN_FUNCTION, OFFSETTER_NONE,
                TOOLTIP_NO_RAIN, 1, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public IAgriGrowCondition withRain(IntPredicate strength) {
        return new GrowConditionSingle<>(RequirementType.RAIN, (str, value) -> strength.test(str) && value, RAIN_FUNCTION, OFFSETTER_NONE,
                TOOLTIP_RAIN, 1, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public IAgriGrowCondition noSnow(IntPredicate strength) {
        return new GrowConditionSingle<>(RequirementType.SNOW, (str, value) -> strength.test(str) && !value, SNOW_FUNCTION, OFFSETTER_NONE,
                TOOLTIP_NO_SNOW, 1, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public IAgriGrowCondition withSnow(IntPredicate strength) {
        return new GrowConditionSingle<>(RequirementType.RAIN, (str, value) -> strength.test(str) && value, SNOW_FUNCTION, OFFSETTER_NONE,
                TOOLTIP_SNOW, 1, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public IAgriGrowCondition season(BiPredicate<Integer, AgriSeason> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionSingle<>(RequirementType.SEASON, predicate, SEASON_FUNCTION, OFFSETTER_NONE,
                tooltips, 1, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public IAgriGrowCondition structure(BiPredicate<Integer, IForgeStructure> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionSingle<>(RequirementType.STRUCTURE, (str, stream) -> stream.anyMatch(structure -> predicate.test(str, structure)),
                STRUCTURE_FUNCTION, OFFSETTER_NONE, tooltips, 1, IAgriGrowCondition.CacheType.FULL);
    }
}
