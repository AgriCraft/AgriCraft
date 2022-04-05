package com.infinityraider.agricraft.impl.v1.requirement;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.*;
import com.infinityraider.agricraft.impl.v1.plant.NoWeed;
import com.infinityraider.infinitylib.utility.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;

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
                                             List<Component> tooltips) {
        return new GrowConditionBase<>(
                RequirementType.SOIL,
                response,
                Functions.SOIL,
                Offsetters.NONE,
                tooltips,
                1,
                IAgriGrowCondition.CacheType.BLOCK_UPDATE
        );
    }

    @Override
    protected <P extends IAgriSoil.SoilProperty> GrowConditionBase<P> soilProperty(
            BiFunction<Integer, P, IAgriGrowthResponse> response, Function<IAgriSoil, P> mapper, P invalid, List<Component> tooltips) {
        return new GrowConditionBase<>(
                RequirementType.SOIL,
                response,
                Functions.soilProperty(mapper),
                Offsetters.NONE,
                tooltips,
                1,
                IAgriGrowCondition.CacheType.BLOCK_UPDATE
        );
    }

    @Override
    public GrowConditionBase<Integer> light(
            BiFunction<Integer, Integer, IAgriGrowthResponse> response, List<Component> tooltips) {
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
            BiFunction<Integer, Integer, IAgriGrowthResponse> response, List<Component> tooltips) {
        return new GrowConditionBase<>(
                RequirementType.REDSTONE,
                response,
                Functions.REDSTONE,
                Offsetters.NONE,
                tooltips,
                1,
                IAgriGrowCondition.CacheType.BLOCK_UPDATE
        );
    }

    @Override
    public GrowConditionBase<Fluid> fluid(BiFunction<Integer, Fluid, IAgriGrowthResponse> response,
                                          List<Component> tooltips) {
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
            BiFunction<Integer, Biome, IAgriGrowthResponse> response, List<Component> tooltips) {
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
    public GrowConditionBase<ResourceKey<Level>> dimensionFromKey(
            BiFunction<Integer, ResourceKey<Level>, IAgriGrowthResponse> response, List<Component> tooltips) {
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
                                                              List<Component> tooltips) {
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
                                             List<Component> tooltips) {
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
                                        List<Component> tooltips) {
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
                                                                   BlockPos minOffset, BlockPos maxOffset, List<Component> tooltips) {
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
    public GrowConditionBase<Stream<BlockEntity>> tileEntitiesNearby(RequirementType type, BiFunction<Integer, Stream<BlockEntity>, IAgriGrowthResponse> response,
                                                                     BlockPos minOffset, BlockPos maxOffset, List<Component> tootlips) {
        BlockPos range = maxOffset.subtract(minOffset);
        return new GrowConditionBase<>(
                type,
                response,
                Functions.tileEntity(minOffset, maxOffset),
                Offsetters.NONE,
                tootlips,
                range.getX()*range.getY()*range.getZ(),
                IAgriGrowCondition.CacheType.NONE
        );
    }

    @Override
    public GrowConditionBase<Stream<Entity>> entitiesNearby(BiFunction<Integer, Stream<Entity>, IAgriGrowthResponse> response,
                                                            double range, List<Component> tooltips) {
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
    public GrowConditionBase<Boolean> rain(BiFunction<Integer, Boolean, IAgriGrowthResponse> response, List<Component> tooltips) {
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
    public GrowConditionBase<Boolean> snow(BiFunction<Integer, Boolean, IAgriGrowthResponse> response, List<Component> tooltips) {
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
                                                List<Component> tooltips) {
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
    public GrowConditionBase<Stream<StructureFeature<?>>> structure(BiFunction<Integer, Stream<StructureFeature<?>>, IAgriGrowthResponse> response,
                                                                    List<Component> tooltips) {
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
        private static final BiFunction<Level, BlockPos, IAgriSoil> SOIL = (world, pos) ->
                AgriApi.getCrop(world, pos).flatMap(IAgriCrop::getSoil).orElse(NoSoil.getInstance());

        private static final BiFunction<Level, BlockPos, Integer> LIGHT = Level::getLightEmission;

        private static final BiFunction<Level, BlockPos, Fluid> FLUID = (world, pos) -> world.getBlockState(pos).getFluidState().getType();

        private static final BiFunction<Level, BlockPos, Integer> REDSTONE = Level::getBestNeighborSignal;

        private static final BiFunction<Level, BlockPos, Biome> BIOME = (level, pos) -> level.getBiome(pos).value();

        private static final Function<Level, ResourceKey<Level>> DIMENSION_KEY = Level::dimension;

        private static final Function<Level, DimensionType> DIMENSION_TYPE = Level::dimensionType;
        private static final Function<Level, Long> TIME = Level::getDayTime;

        private static final BiFunction<Level, BlockPos, Boolean> RAIN = (world, pos) ->
                world.isRainingAt(pos) && world.getBiome(pos).value().getPrecipitation() == Biome.Precipitation.RAIN;

        private static final BiFunction<Level, BlockPos, Boolean> SNOW = (world, pos) ->
                world.isRainingAt(pos) && world.getBiome(pos).value().getPrecipitation() == Biome.Precipitation.SNOW;

        private static final BiFunction<Level, BlockPos, AgriSeason> SEASON = (world, pos) ->
                AgriApi.getSeasonLogic().getSeason(world, pos);

        private static final BiFunction<Level, BlockPos, Optional<IAgriCrop>> CROP = AgriApi::getCrop;

        private static final BiFunction<Level, BlockPos, Stream<StructureFeature<?>>> STRUCTURE = (world, pos) ->
                world.getChunkAt(pos).getAllReferences().entrySet().stream()
                        .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                        .map(Map.Entry::getKey).map(feature -> (Object) feature)
                        .filter(obj -> obj instanceof StructureFeature<?>)  // TODO figure out why the f intellij is bitching about casting here
                        .map(obj -> (StructureFeature<?>) obj);

        private static <P extends IAgriSoil.SoilProperty> BiFunction<Level, BlockPos, P> soilProperty(Function<IAgriSoil, P> mapper) {
            return SOIL.andThen(mapper);
        }

        private static BiFunction<Level, BlockPos, Stream<Entity>> entity(double range) {
            return (world, pos) ->
                    world.getEntities(
                            null, new AABB(pos.offset(-range, -range, -range), pos.offset(range, range, range))).stream();
        }

        private static BiFunction<Level, BlockPos, Stream<BlockState>> blockstate(BlockPos min, BlockPos max) {
            return (world, pos) -> WorldHelper.streamPositions(pos.offset(min), pos.offset(max)).map(world::getBlockState);
        }

        private static BiFunction<Level, BlockPos, Stream<BlockEntity>> tileEntity(BlockPos min, BlockPos max) {
            return (world, pos) -> WorldHelper.streamTiles(world, pos.offset(min), pos.offset(max), BlockEntity.class);
        }

        private Functions () {}
    }

    private static final class Offsetters {
        private static final UnaryOperator<BlockPos> NONE = pos -> pos;

        private Offsetters() {}
    }
}
