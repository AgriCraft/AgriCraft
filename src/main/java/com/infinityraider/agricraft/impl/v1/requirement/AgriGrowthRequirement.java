package com.infinityraider.agricraft.impl.v1.requirement;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthRequirement;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowCondition;
import com.infinityraider.agricraft.api.v1.requirement.RequirementType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.UnaryOperator;

public class AgriGrowthRequirement implements IAgriGrowthRequirement {
    private static final IAgriGrowthRequirement NONE = new IAgriGrowthRequirement() {
        @Nonnull
        @Override
        public Set<IAgriGrowCondition> getGrowConditions() {
            return ImmutableSet.of();
        }

        @Override
        public boolean isSoilHumidityAccepted(IAgriSoil.Humidity humidity, int strength) {
            return true;
        }

        @Override
        public boolean isSoilAcidityAccepted(IAgriSoil.Acidity acidity, int strength) {
            return true;
        }

        @Override
        public boolean isSoilNutrientsAccepted(IAgriSoil.Nutrients nutrients, int strength) {
            return true;
        }

        @Override
        public boolean isLightLevelAccepted(int light, int strength) {
            return true;
        }
    };

    public static IAgriGrowthRequirement.Builder getBuilder() {
        return new Builder();
    }

    public static IAgriGrowthRequirement getNone() {
        return NONE;
    }

    private final Set<IAgriGrowCondition> conditions;

    private final GrowConditionSingle<IAgriSoil.Humidity> humidity;
    private final GrowConditionSingle<IAgriSoil.Acidity> acidity;
    private final GrowConditionSingle<IAgriSoil.Nutrients> nutrients;
    private final GrowConditionSingle<Integer> lightLevel;

    private AgriGrowthRequirement(Set<IAgriGrowCondition> conditions,
                                  GrowConditionSingle<IAgriSoil.Humidity> humidity,
                                  GrowConditionSingle<IAgriSoil.Acidity> acidity,
                                  GrowConditionSingle<IAgriSoil.Nutrients> nutrients,
                                  GrowConditionSingle<Integer> lightLevel) {
        this.humidity = humidity;
        this.acidity = acidity;
        this.nutrients = nutrients;
        this.lightLevel = lightLevel;
        ImmutableSet.Builder<IAgriGrowCondition> builder = ImmutableSet.builder();
        builder.add(this.humidity, this.acidity, this.nutrients, this.lightLevel);
        builder.addAll(conditions);
        this.conditions = builder.build();
    }

    @Nonnull
    @Override
    public Set<IAgriGrowCondition> getGrowConditions() {
        return this.conditions;
    }

    @Override
    public boolean isSoilHumidityAccepted(IAgriSoil.Humidity humidity, int strength) {
        return this.humidity.test(humidity, strength);
    }

    @Override
    public boolean isSoilAcidityAccepted(IAgriSoil.Acidity acidity, int strength) {
        return this.acidity.test(acidity, strength);
    }

    @Override
    public boolean isSoilNutrientsAccepted(IAgriSoil.Nutrients nutrients, int strength) {
        return this.nutrients.test(nutrients, strength);
    }

    @Override
    public boolean isLightLevelAccepted(int light, int strength) {
        return this.lightLevel.test(light, strength);
    }

    private static class Builder implements IAgriGrowthRequirement.Builder {
        private static final BiFunction<World, BlockPos, IAgriSoil.Humidity> HUMIDITY_GETTER = (world, pos) ->
                AgriApi.getSoilRegistry().valueOf(world.getBlockState(pos)).map(IAgriSoil::getHumidity).orElse(IAgriSoil.Humidity.INVALID);
        private static final BiFunction<World, BlockPos, IAgriSoil.Acidity> ACIDITY_GETTER = (world, pos) ->
                AgriApi.getSoilRegistry().valueOf(world.getBlockState(pos)).map(IAgriSoil::getAcidity).orElse(IAgriSoil.Acidity.INVALID);
        private static final BiFunction<World, BlockPos, IAgriSoil.Nutrients> NUTRIENTS_GETTER = (world, pos) ->
                AgriApi.getSoilRegistry().valueOf(world.getBlockState(pos)).map(IAgriSoil::getNutrients).orElse(IAgriSoil.Nutrients.INVALID);
        private static final BiFunction<World, BlockPos, Integer> LIGHT_LEVEL_GETTER = IWorldReader::getLight;

        private static final UnaryOperator<BlockPos> CROP = pos -> pos;
        private static final UnaryOperator<BlockPos> SOIL = BlockPos::down;

        private static final Set<ITextComponent> HUMIDITY_DESCRIPTION = ImmutableSet.of();
        private static final Set<ITextComponent> ACIDITY_DESCRIPTION = ImmutableSet.of();
        private static final Set<ITextComponent> NUTRIENT_DESCRIPTION = ImmutableSet.of();
        private static final Set<ITextComponent> LIGHT_LEVEL_DESCRIPTION = ImmutableSet.of();

        private final Set<IAgriGrowCondition> conditions;

        private GrowConditionSingle<IAgriSoil.Humidity> humidity;
        private GrowConditionSingle<IAgriSoil.Acidity> acidity;
        private GrowConditionSingle<IAgriSoil.Nutrients> nutrients;
        private GrowConditionSingle<Integer> lightLevel;

        private Builder() {
            this.conditions = Sets.newIdentityHashSet();
        }

        @Override
        public IAgriGrowthRequirement build() {
            if(this.humidity == null) {
                throw new IllegalStateException("Can not build an IAgriGrowthRequirement without initializing the humidity rule");
            }
            if(this.acidity == null) {
                throw new IllegalStateException("Can not build an IAgriGrowthRequirement without initializing the acidity rule");
            }
            if(this.nutrients == null) {
                throw new IllegalStateException("Can not build an IAgriGrowthRequirement without initializing the nutrients rule");
            }
            if(this.lightLevel == null) {
                throw new IllegalStateException("Can not build an IAgriGrowthRequirement without initializing the lightLevel rule");
            }
            return new AgriGrowthRequirement(this.conditions, this.humidity, this.acidity, this.nutrients, this.lightLevel);
        }

        @Override
        public IAgriGrowthRequirement.Builder defineHumidity(BiPredicate<IAgriSoil.Humidity, Integer> predicate) {
            this.humidity = new GrowConditionSingle<>(RequirementType.SOIL, predicate, HUMIDITY_GETTER, SOIL,
                    HUMIDITY_DESCRIPTION, 1, IAgriGrowCondition.CacheType.BLOCK_UPDATE);
            return this;
        }

        @Override
        public IAgriGrowthRequirement.Builder defineAcidity(BiPredicate<IAgriSoil.Acidity, Integer> predicate) {
            this.acidity = new GrowConditionSingle<>(RequirementType.SOIL, predicate, ACIDITY_GETTER, SOIL,
                    ACIDITY_DESCRIPTION, 1, IAgriGrowCondition.CacheType.BLOCK_UPDATE);
            return this;
        }

        @Override
        public IAgriGrowthRequirement.Builder defineNutrients(BiPredicate<IAgriSoil.Nutrients, Integer> predicate) {
            this.nutrients = new GrowConditionSingle<>(RequirementType.SOIL, predicate, NUTRIENTS_GETTER, SOIL,
                    NUTRIENT_DESCRIPTION, 1, IAgriGrowCondition.CacheType.BLOCK_UPDATE);
            return this;
        }

        @Override
        public IAgriGrowthRequirement.Builder defineLightLevel(BiPredicate<Integer, Integer> predicate) {
            this.lightLevel = new GrowConditionSingle<>(RequirementType.LIGHT, predicate, LIGHT_LEVEL_GETTER, CROP,
                    LIGHT_LEVEL_DESCRIPTION, 1, IAgriGrowCondition.CacheType.NONE);
            return this;
        }

        @Override
        public IAgriGrowthRequirement.Builder addCondition(IAgriGrowCondition condition) {
            this.conditions.add(condition);
            return this;
        }
    }
}
