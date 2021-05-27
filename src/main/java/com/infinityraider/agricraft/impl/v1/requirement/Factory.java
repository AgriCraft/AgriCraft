package com.infinityraider.agricraft.impl.v1.requirement;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.*;
import com.infinityraider.agricraft.impl.v1.plant.NoWeed;
import com.infinityraider.infinitylib.utility.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.*;
import java.util.stream.Stream;

public class Factory extends FactoryAbstract {
    private static final Factory INSTANCE = new Factory();

    public static IDefaultGrowConditionFactory getInstance() {
        return INSTANCE;
    }

    protected Factory() {}

    @Override
    public GrowConditionBase<IAgriSoil> soil(BiFunction<Integer, IAgriSoil, IAgriGrowthResponse> response,
                                             List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(
                RequirementType.SOIL,
                response,
                Functions.SOIL,
                Offsetters.SOIL,
                tooltips,
                1,
                IAgriGrowCondition.CacheType.BLOCK_UPDATE
        );
    }

    @Override
    protected <P extends IAgriSoil.SoilProperty> GrowConditionBase<P> soilProperty(
            BiFunction<Integer, P, IAgriGrowthResponse> response, Function<IAgriSoil, P> mapper, P invalid, List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(
                RequirementType.SOIL,
                response,
                Functions.soilProperty(mapper),
                Offsetters.SOIL,
                tooltips,
                1,
                IAgriGrowCondition.CacheType.BLOCK_UPDATE
        );
    }

    @Override
    public GrowConditionBase<Integer> light(
            BiFunction<Integer, Integer, IAgriGrowthResponse> response, List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(
                RequirementType.LIGHT,
                response,
                Functions.LIGHT,
                Offsetters.NONE,
                tooltips,
                1,
                IAgriGrowCondition.CacheType.NONE
        );
    }

    @Override
    public GrowConditionBase<Integer> redstone(
            BiFunction<Integer, Integer, IAgriGrowthResponse> response, List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(
                RequirementType.REDSTONE,
                response,
                Functions.REDSTONE,
                Offsetters.SOIL,
                tooltips,
                1,
                IAgriGrowCondition.CacheType.BLOCK_UPDATE
        );
    }

    @Override
    public GrowConditionBase<Fluid> fluid(BiFunction<Integer, Fluid, IAgriGrowthResponse> response,
                                          List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(
                RequirementType.LIQUID,
                response,
                Functions.FLUID,
                Offsetters.NONE,
                tooltips,
                1,
                IAgriGrowCondition.CacheType.BLOCK_UPDATE
        );
    }

    @Override
    public GrowConditionBase<Biome> biome(
            BiFunction<Integer, Biome, IAgriGrowthResponse> response, List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(
                RequirementType.BIOME,
                response,
                Functions.BIOME,
                Offsetters.NONE,
                tooltips,
                1,
                IAgriGrowCondition.CacheType.FULL
        );
    }

    @Override
    public GrowConditionBase<RegistryKey<World>> dimensionFromKey(
            BiFunction<Integer, RegistryKey<World>, IAgriGrowthResponse> response, List<ITextComponent> tooltips) {
        return new GrowConditionAmbient<>(
                RequirementType.DIMENSION,
                response,
                Functions.DIMENSION_KEY,
                tooltips,
                IAgriGrowCondition.CacheType.FULL
        );
    }

    @Override
    public GrowConditionBase<DimensionType> dimensionFromType(BiFunction<Integer, DimensionType, IAgriGrowthResponse> response,
                                                              List<ITextComponent> tooltips) {
        return new GrowConditionAmbient<>(
                RequirementType.DIMENSION,
                response,
                Functions.DIMENSION_TYPE,
                tooltips,
                IAgriGrowCondition.CacheType.FULL
        );
    }

    @Override
    public GrowConditionBase<IAgriWeed> weed(BiFunction<Integer, IAgriWeed, IAgriGrowthResponse> response,
                                             List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(
                RequirementType.WEEDS,
                response,
                Functions.CROP.andThen(opt -> opt.map(IAgriCrop::getWeeds).orElse(NoWeed.getInstance())),
                Offsetters.NONE,
                tooltips,
                1,
                IAgriGrowCondition.CacheType.BLOCK_UPDATE
        );
    }

    @Override
    public GrowConditionBase<Long> time(BiFunction<Integer, Long, IAgriGrowthResponse> response,
                                        List<ITextComponent> tooltips) {
        return new GrowConditionAmbient<>(
                RequirementType.TIME,
                response,
                Functions.TIME,
                tooltips,
                IAgriGrowCondition.CacheType.FULL
        );
    }

    @Override
    public GrowConditionBase<Stream<BlockState>> blockStatesNearby(RequirementType type, BiFunction<Integer, Stream<BlockState>, IAgriGrowthResponse> response,
                                                                   BlockPos minOffset, BlockPos maxOffset, List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(
                type,
                response,
                Functions.blockstate(minOffset, maxOffset),
                Offsetters.NONE,
                tooltips,
                1,
                IAgriGrowCondition.CacheType.NONE
        );
    }

    @Override
    public GrowConditionBase<Stream<Entity>> entitiesNearby(BiFunction<Integer, Stream<Entity>, IAgriGrowthResponse> response,
                                             double range, List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(
                RequirementType.ENTITY,
                response,
                Functions.entity(range),
                Offsetters.NONE,
                tooltips,
                1,
                IAgriGrowCondition.CacheType.NONE
        );
    }

    @Override
    public GrowConditionBase<Boolean> rain(BiFunction<Integer, Boolean, IAgriGrowthResponse> response, List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(
                RequirementType.RAIN,
                response,
                Functions.RAIN,
                Offsetters.NONE,
                tooltips,
                1,
                IAgriGrowCondition.CacheType.NONE
        );
    }

    @Override
    public GrowConditionBase<Boolean> snow(BiFunction<Integer, Boolean, IAgriGrowthResponse> response, List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(
                RequirementType.SNOW,
                response,
                Functions.SNOW,
                Offsetters.NONE,
                tooltips,
                1,
                IAgriGrowCondition.CacheType.NONE
        );
    }

    @Override
    public GrowConditionBase<AgriSeason> season(BiFunction<Integer, AgriSeason, IAgriGrowthResponse> response,
                                                List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(
                RequirementType.SEASON,
                response,
                Functions.SEASON,
                Offsetters.NONE,
                tooltips,
                1,
                IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public GrowConditionBase<Stream<Structure<?>>> structure(BiFunction<Integer, Stream<Structure<?>>, IAgriGrowthResponse> response,
                                                             List<ITextComponent> tooltips) {
        return new GrowConditionBase<>(
                RequirementType.STRUCTURE,
                response,
                Functions.STRUCTURE,
                Offsetters.NONE,
                tooltips,
                1,
                IAgriGrowCondition.CacheType.FULL);
    }

    private static final class Functions {
        private static final BiFunction<World, BlockPos, IAgriSoil> SOIL = (world, pos) -> AgriApi.getSoil(world, pos).orElse(NoSoil.getInstance());

        private static final BiFunction<World, BlockPos, Integer> LIGHT = IWorldReader::getLight;

        private static final BiFunction<World, BlockPos, Fluid> FLUID = (world, pos) -> world.getBlockState(pos).getFluidState().getFluid();

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

        private static <P extends IAgriSoil.SoilProperty> BiFunction<World, BlockPos, P> soilProperty(Function<IAgriSoil, P> mapper) {
            return SOIL.andThen(mapper);
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
}
