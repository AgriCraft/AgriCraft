package com.infinityraider.agricraft.impl.v1.requirement;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.plant.IAgriWeed;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.api.v1.requirement.IDefaultGrowConditionFactory;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowCondition;
import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.extensions.IForgeStructure;

import java.util.List;
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
    private static final BiFunction<World, BlockPos, AgriSeason> SEASON_FUNCTION = (world, pos) -> AgriApi.getSeasonLogic().getSeason(world, pos);

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

    private Factory() {}

    @Override
    protected IAgriGrowCondition statesInRange(int strength, RequirementType type, int min, int max,
                                               BlockPos minOffset, BlockPos maxOffset, Predicate<BlockState> predicate,
                                               List<ITextComponent> tooltips) {
        return new GrowConditionBlockStates(strength, type, min, max, minOffset, maxOffset, predicate, tooltips);
    }

    @Override
    public IAgriGrowCondition light(int strength, IntPredicate predicate, List<ITextComponent> tooltips) {
        return new GrowConditionSingleInt(
                strength, RequirementType.LIGHT, OFFSET_NONE, LIGHT_FUNCTION, predicate, tooltips, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public IAgriGrowCondition redstone(int strength, IntPredicate predicate, List<ITextComponent> tooltips) {
        return new GrowConditionSingleInt(
                strength, RequirementType.LIGHT, OFFSET_SOIL, REDSTONE_FUNCTION, predicate, tooltips, IAgriGrowCondition.CacheType.BLOCK_UPDATE);
    }

    @Override
    public IAgriGrowCondition biome(int strength, Predicate<Biome> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionSingle<>(
                strength, RequirementType.BIOME, OFFSET_NONE, BIOME_FUNCTION, predicate, tooltips, IAgriGrowCondition.CacheType.FULL);
    }

    @Override
    public IAgriGrowCondition dimensionFromKey(int strength, Predicate<RegistryKey<World>> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionAmbient<>(
                strength, RequirementType.DIMENSION, DIMENSION_KEY_FUNCTION, predicate, tooltips, IAgriGrowCondition.CacheType.FULL);
    }

    @Override
    public IAgriGrowCondition dimensionFromType(int strength, Predicate<DimensionType> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionAmbient<>(
                strength, RequirementType.DIMENSION, DIMENSION_TYPE_FUNCTION, predicate, tooltips, IAgriGrowCondition.CacheType.FULL);
    }

    @Override
    public IAgriGrowCondition weed(int strength, BiPredicate<IAgriWeed, IAgriGrowthStage> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionWeeds(strength, OFFSET_NONE, predicate, tooltips);
    }

    @Override
    public IAgriGrowCondition time(int strength, LongPredicate predicate, List<ITextComponent> tooltips) {
        return new GrowConditionAmbientLong(
                strength, RequirementType.TIME, TIME_FUNCTION, predicate, tooltips, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public IAgriGrowCondition entitiesNearby(int strength, Predicate<Entity> predicate, double range, int min, int max,
                                             List<ITextComponent> tooltips) {
        return new GrowConditionEntities(strength, predicate, OFFSET_NONE, range, min, max, tooltips);
    }

    @Override
    public IAgriGrowCondition noRain(int strength) {
        return new GrowConditionBoolean(
                strength, RequirementType.RAIN, OFFSET_NONE, RAIN_FUNCTION.negate(), TOOLTIP_NO_RAIN, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public IAgriGrowCondition withRain(int strength) {
        return new GrowConditionBoolean(
                strength, RequirementType.RAIN, OFFSET_NONE, RAIN_FUNCTION, TOOLTIP_RAIN, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public IAgriGrowCondition noSnow(int strength) {
        return new GrowConditionBoolean(
                strength, RequirementType.RAIN, OFFSET_NONE, SNOW_FUNCTION.negate(), TOOLTIP_NO_SNOW, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public IAgriGrowCondition withSnow(int strength) {
        return new GrowConditionBoolean(
                strength, RequirementType.RAIN, OFFSET_NONE, SNOW_FUNCTION, TOOLTIP_SNOW, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public IAgriGrowCondition season(int strength, Predicate<AgriSeason> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionSingle<>(
                strength, RequirementType.SEASON, OFFSET_NONE, SEASON_FUNCTION, predicate, tooltips, IAgriGrowCondition.CacheType.NONE);
    }

    @Override
    public IAgriGrowCondition structure(int strength, Predicate<IForgeStructure> predicate, List<ITextComponent> tooltips) {
        return new GrowConditionStructure(strength, predicate, OFFSET_NONE, tooltips);
    }
}
