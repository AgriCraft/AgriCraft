package com.infinityraider.agricraft.core.requirement;

import com.infinityraider.agricraft.api.v1.plant.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.IDefaultGrowConditionFactory;
import com.infinityraider.agricraft.api.v1.requirement.IGrowCondition;
import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import com.infinityraider.agricraft.api.v1.soil.IAgriSoil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.extensions.IForgeStructure;

import java.util.Collection;
import java.util.function.*;

public class Factory extends FactoryAbstract {
    private static final Factory INSTANCE = new Factory();

    private static final ToIntBiFunction<World, BlockPos> LIGHT_FUNCTION = IWorldReader::getLight;
    private static final ToIntBiFunction<World, BlockPos> REDSTONE_FUNCTION = World::getRedstonePowerFromNeighbors;
    private static final BiFunction<World, BlockPos, Biome> BIOME_FUNCTION = World::getBiome;
    private static final Function<World, RegistryKey<World>> DIMENSION_KEY_FUNCTION = World::getDimensionKey;
    private static final Function<World, DimensionType> DIMENSION_TYPE_FUNCTION = World::getDimensionType;
    private static final ToLongFunction<World> TIME_FUNCTION = World::getDayTime;
    private static final BiPredicate<World, BlockPos> RAIN_FUNCTION = (world, pos) -> world.isRainingAt(pos) && world.getBiome(pos).getPrecipitation() == Biome.RainType.RAIN;
    private static final BiPredicate<World, BlockPos> SNOW_FUNCTION = (world, pos) -> world.isRainingAt(pos) && world.getBiome(pos).getPrecipitation() == Biome.RainType.SNOW;

    public static IDefaultGrowConditionFactory getInstance() {
        return INSTANCE;
    }

    private Factory() {}

    @Override
    protected IGrowCondition statesInRange(int strength, RequirementType type, int min, int max, BlockPos minOffset, BlockPos maxOffset, Predicate<BlockState> predicate) {
        return new GrowConditionBlockStates(strength, type, min, max, minOffset, maxOffset, predicate);
    }

    @Override
    public IGrowCondition light(int strength, IntPredicate predicate) {
        return new GrowConditionSingleInt(strength, RequirementType.LIGHT, OFFSET_NONE, LIGHT_FUNCTION, predicate);
    }

    @Override
    public IGrowCondition redstone(int strength, IntPredicate predicate) {
        return new GrowConditionSingleInt(strength, RequirementType.LIGHT, OFFSET_SOIL, REDSTONE_FUNCTION, predicate);
    }

    @Override
    public IGrowCondition biome(int strength, Predicate<Biome> predicate) {
        return new GrowConditionSingle<>(strength, RequirementType.BIOME, OFFSET_NONE, BIOME_FUNCTION, predicate);
    }

    @Override
    public IGrowCondition dimensionFromKey(int strength, Predicate<RegistryKey<World>> predicate) {
        return new GrowConditionAmbient<>(strength, RequirementType.DIMENSION, DIMENSION_KEY_FUNCTION, predicate);
    }

    @Override
    public IGrowCondition dimensionFromType(int strength, Predicate<DimensionType> predicate) {
        return new GrowConditionAmbient<>(strength, RequirementType.DIMENSION, DIMENSION_TYPE_FUNCTION, predicate);
    }

    @Override
    public IGrowCondition weed(int strength, BiPredicate<IAgriWeed, IAgriGrowthStage> predicate) {
        return new GrowConditionWeeds(strength, OFFSET_NONE, predicate);
    }

    @Override
    public IGrowCondition time(int strength, LongPredicate predicate) {
        return new GrowConditionAmbientLong(strength, RequirementType.TIME, TIME_FUNCTION, predicate);
    }

    @Override
    public IGrowCondition entitiesNearby(int strength, Predicate<Entity> predicate, double range, int min, int max) {
        return new GrowConditionEntities(strength, predicate, OFFSET_NONE, range, min, max);
    }

    @Override
    public IGrowCondition noRain(int strength) {
        return new GrowConditionBoolean(strength, RequirementType.RAIN, OFFSET_NONE, RAIN_FUNCTION.negate());
    }

    @Override
    public IGrowCondition withRain(int strength) {
        return new GrowConditionBoolean(strength, RequirementType.RAIN, OFFSET_NONE, RAIN_FUNCTION);
    }

    @Override
    public IGrowCondition noSnow(int strength) {
        return new GrowConditionBoolean(strength, RequirementType.RAIN, OFFSET_NONE, SNOW_FUNCTION.negate());
    }

    @Override
    public IGrowCondition withSnow(int strength) {
        return new GrowConditionBoolean(strength, RequirementType.RAIN, OFFSET_NONE, SNOW_FUNCTION);
    }

    @Override
    public IGrowCondition structure(int strength, Predicate<IForgeStructure> predicate) {
        return new GrowConditionStructure(strength, predicate, OFFSET_NONE);
    }
}
