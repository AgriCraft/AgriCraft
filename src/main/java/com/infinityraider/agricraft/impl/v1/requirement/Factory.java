package com.infinityraider.agricraft.impl.v1.requirement;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.*;
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

    public static IDefaultGrowConditionFactory getInstance() {
        return INSTANCE;
    }

    protected Factory() {}

    @Override
    protected GrowConditionBase<Stream<BlockState>> statesInRange(RequirementType type, BiPredicate<Integer, BlockState> predicate, int min, int max,
                                                                  BlockPos minOffset, BlockPos maxOffset,
                                                                  List<ITextComponent> tooltips) {
        return new GrowConditionMulti<>(type, predicate, Functions.blockstate(minOffset, maxOffset), min, max, Offsetters.NONE,
                tooltips, (maxOffset.getX() - minOffset.getX())*(maxOffset.getX() - minOffset.getX())*(maxOffset.getX() - minOffset.getX()),
                IAgriGrowCondition.CacheType.BLOCK_UPDATE, WorldHelper.streamPositions(minOffset, maxOffset).collect(Collectors.toSet()));
    }

    @Override
    protected <P extends IAgriSoil.SoilProperty> GrowConditionBase<P> soilProperty(
            BiPredicate<Integer, P> predicate, Function<IAgriSoil, P> mapper, P invalid, List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(RequirementType.SOIL, predicate, Functions.soilProperty(mapper, invalid), Offsetters.SOIL,
                tooltips, 1, IAgriGrowCondition.CacheType.BLOCK_UPDATE);
    }

    @Override
    public GrowConditionBase<Integer> light(BiPredicate<Integer, Integer> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(RequirementType.LIGHT, predicate, Functions.LIGHT, Offsetters.NONE,
                tooltips, 1, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public GrowConditionBase<Integer> redstone(BiPredicate<Integer, Integer> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(RequirementType.REDSTONE, predicate, Functions.REDSTONE, Offsetters.SOIL,
                tooltips, 1, IAgriGrowCondition.CacheType.BLOCK_UPDATE);
    }

    @Override
    public GrowConditionBase<Biome> biome(BiPredicate<Integer, Biome> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(RequirementType.BIOME, predicate, Functions.BIOME, Offsetters.NONE,
                tooltips, 1, IAgriGrowCondition.CacheType.FULL);
    }

    @Override
    public GrowConditionBase<RegistryKey<World>> dimensionFromKey(BiPredicate<Integer, RegistryKey<World>> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionAmbient<>(RequirementType.DIMENSION, predicate, Functions.DIMENSION_KEY,
                tooltips, IAgriGrowCondition.CacheType.FULL);
    }

    @Override
    public GrowConditionBase<DimensionType> dimensionFromType(BiPredicate<Integer, DimensionType> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionAmbient<>(RequirementType.DIMENSION, predicate, Functions.DIMENSION_TYPE,
                tooltips, IAgriGrowCondition.CacheType.FULL);
    }

    @Override
    public GrowConditionBase<Optional<IAgriCrop>> weed(TriPredicate<Integer, IAgriWeed, IAgriGrowthStage> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(RequirementType.WEEDS, (str, opt) -> opt.map(crop -> predicate.test(str, crop.getWeeds(), crop.getWeedGrowthStage())).orElse(false),
                Functions.CROP, Offsetters.NONE, tooltips, 1, IAgriGrowCondition.CacheType.BLOCK_UPDATE);
    }

    @Override
    public GrowConditionBase<Long> time(BiPredicate<Integer, Long> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionAmbient<>(RequirementType.TIME, predicate, Functions.TIME,
                tooltips, IAgriGrowCondition.CacheType.FULL);
    }

    @Override
    public GrowConditionBase<Stream<Entity>> entitiesNearby(BiPredicate<Integer, Entity> predicate, double range, int min, int max,
                                             List<ITextComponent> tooltips) {
        return new GrowConditionMulti<>(RequirementType.ENTITY, predicate, Functions.entity(range), min, max, Offsetters.NONE,
                tooltips, 1, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public GrowConditionBase<Boolean> noRain(IntPredicate strength) {
        return new GrowConditionBase<>(RequirementType.RAIN, (str, value) -> strength.test(str) && !value, Functions.RAIN, Offsetters.NONE,
                Tooltips.NO_RAIN, 1, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public GrowConditionBase<Boolean> withRain(IntPredicate strength) {
        return new GrowConditionBase<>(RequirementType.RAIN, (str, value) -> strength.test(str) && value, Functions.RAIN, Offsetters.NONE,
                Tooltips.RAIN, 1, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public GrowConditionBase<Boolean> noSnow(IntPredicate strength) {
        return new GrowConditionBase<>(RequirementType.SNOW, (str, value) -> strength.test(str) && !value, Functions.SNOW, Offsetters.NONE,
                Tooltips.NO_SNOW, 1, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public GrowConditionBase<Boolean> withSnow(IntPredicate strength) {
        return new GrowConditionBase<>(RequirementType.RAIN, (str, value) -> strength.test(str) && value, Functions.SNOW, Offsetters.NONE,
                Tooltips.SNOW, 1, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public GrowConditionBase<AgriSeason> season(BiPredicate<Integer, AgriSeason> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(RequirementType.SEASON, predicate, Functions.SEASON, Offsetters.NONE,
                tooltips, 1, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public GrowConditionBase<Stream<Structure<?>>> structure(BiPredicate<Integer, IForgeStructure> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(RequirementType.STRUCTURE, (str, stream) -> stream.anyMatch(structure -> predicate.test(str, structure)),
                Functions.STRUCTURE, Offsetters.NONE, tooltips, 1, IAgriGrowCondition.CacheType.FULL);
    }

    private static final class Functions {
        private static final BiFunction<World, BlockPos, Integer> LIGHT = IWorldReader::getLight;

        private static final BiFunction<World, BlockPos, Integer> REDSTONE = World::getRedstonePowerFromNeighbors;

        private static final BiFunction<World, BlockPos, Biome> BIOME = World::getBiome;

        private static final Function<World, RegistryKey<World>> DIMENSION_KEY = World::getDimensionKey;

        private static final Function<World, DimensionType> DIMENSION_TYPE = World::getDimensionType;
        private static final Function<World, Long> TIME = World::getDayTime;

        private static final BiFunction<World, BlockPos, Boolean> RAIN = (world, pos) ->
                world.isRainingAt(pos) && world.getBiome(pos).getPrecipitation() == Biome.RainType.RAIN;

        private static final BiFunction<World, BlockPos, Boolean> SNOW = (world, pos) ->
                world.isRainingAt(pos) && world.getBiome(pos).getPrecipitation() == Biome.RainType.SNOW;

        private static final BiFunction<World, BlockPos, AgriSeason> SEASON = (world, pos) ->
                AgriApi.getSeasonLogic().getSeason(world, pos);

        private static final BiFunction<World, BlockPos, Optional<IAgriCrop>> CROP = AgriApi::getCrop;

        private static final BiFunction<World, BlockPos, Stream<Structure<?>>> STRUCTURE = (world, pos) ->
                world.getChunkAt(pos).getStructureReferences().entrySet().stream()
                        .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                        .map(Map.Entry::getKey);

        private static <P extends IAgriSoil.SoilProperty> BiFunction<World, BlockPos, P> soilProperty(Function<IAgriSoil, P> mapper, P invalid) {
            return (world, pos) -> AgriApi.getSoilRegistry().valueOf(world.getBlockState(pos)).map(mapper).orElse(invalid);
        }

        private static BiFunction<World, BlockPos, Stream<Entity>> entity(double range) {
            return (world, pos) ->
                    world.getEntitiesWithinAABBExcludingEntity(
                            null, new AxisAlignedBB(pos.add(-range, -range, -range), pos.add(range, range, range))).stream();
        }

        private static BiFunction<World, BlockPos, Stream<BlockState>> blockstate(BlockPos min, BlockPos max) {
            return (world, pos) -> WorldHelper.streamPositions(pos.add(min), pos.add(max)).map(world::getBlockState);
        }

        private Functions () {}
    }

    private static final class Offsetters {
        private static final UnaryOperator<BlockPos> NONE = pos -> pos;
        private static final UnaryOperator<BlockPos> SOIL = BlockPos::down;

        private Offsetters() {}
    }

    private static final class Tooltips {

        private static final List<ITextComponent> NO_RAIN = ImmutableList.of(
                new TranslationTextComponent(AgriCraft.instance.getModId() + ".tooltip.growth_req.no_rain"));
        private static final List<ITextComponent> RAIN = ImmutableList.of(
                new TranslationTextComponent(AgriCraft.instance.getModId() + ".tooltip.growth_req.rain"));
        private static final List<ITextComponent> NO_SNOW = ImmutableList.of(
                new TranslationTextComponent(AgriCraft.instance.getModId() + ".tooltip.growth_req.no_snow"));
        private static final List<ITextComponent> SNOW = ImmutableList.of(
                new TranslationTextComponent(AgriCraft.instance.getModId() + ".tooltip.growth_req.snow"));

        private Tooltips() {}
    }
}
