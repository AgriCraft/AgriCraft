package com.infinityraider.agricraft.impl.v1.requirement;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.requirement.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.function.*;

public class AgriGrowthRequirement implements IAgriGrowthRequirement {
    public static IAgriGrowthRequirement.Builder getBuilder() {
        return new Builder();
    }

    public static IAgriGrowthRequirement getNone() {
        return NONE;
    }

    private final Set<IAgriGrowCondition> conditions;

    private final GrowConditionBase<IAgriSoil.Humidity> humidity;
    private final GrowConditionBase<IAgriSoil.Acidity> acidity;
    private final GrowConditionBase<IAgriSoil.Nutrients> nutrients;
    private final GrowConditionBase<Integer> lightLevel;
    private final GrowConditionBase<AgriSeason> season;
    private final GrowConditionBase<Fluid> fluid;

    private AgriGrowthRequirement(Set<IAgriGrowCondition> conditions,
                                  GrowConditionBase<IAgriSoil.Humidity> humidity,
                                  GrowConditionBase<IAgriSoil.Acidity> acidity,
                                  GrowConditionBase<IAgriSoil.Nutrients> nutrients,
                                  GrowConditionBase<Integer> lightLevel,
                                  GrowConditionBase<AgriSeason> season,
                                  GrowConditionBase<Fluid> fluid) {
        this.humidity = humidity;
        this.acidity = acidity;
        this.nutrients = nutrients;
        this.lightLevel = lightLevel;
        this.season = season;
        this.fluid = fluid;
        ImmutableSet.Builder<IAgriGrowCondition> builder = ImmutableSet.builder();
        builder.add(this.humidity, this.acidity, this.nutrients, this.lightLevel, this.season, this.fluid);
        builder.addAll(conditions);
        this.conditions = builder.build();
    }

    @Nonnull
    @Override
    public Set<IAgriGrowCondition> getGrowConditions() {
        return this.conditions;
    }

    @Override
    public IAgriGrowthResponse getSoilHumidityResponse(IAgriSoil.Humidity humidity, int strength) {
        return this.humidity.apply(strength, humidity);
    }

    @Override
    public IAgriGrowthResponse getSoilAcidityResponse(IAgriSoil.Acidity acidity, int strength) {
        return this.acidity.apply(strength, acidity);
    }

    @Override
    public IAgriGrowthResponse getSoilNutrientsResponse(IAgriSoil.Nutrients nutrients, int strength) {
        return this.nutrients.apply(strength, nutrients);
    }

    @Override
    public IAgriGrowthResponse getLightLevelResponse(int light, int strength) {
        return this.lightLevel.apply(strength, light);
    }

    @Override
    public IAgriGrowthResponse getSeasonResponse(AgriSeason season, int strength) {
        return this.season.apply(strength, season);
    }

    @Override
    public IAgriGrowthResponse getFluidResponse(Fluid fluid, int strength) {
        return this.fluid.apply(strength, fluid);
    }

    private static class Builder extends Factory implements IAgriGrowthRequirement.Builder {

        private final Set<IAgriGrowCondition> conditions;

        private GrowConditionBase<IAgriSoil.Humidity> humidity;
        private GrowConditionBase<IAgriSoil.Acidity> acidity;
        private GrowConditionBase<IAgriSoil.Nutrients> nutrients;
        private GrowConditionBase<Integer> lightLevel;
        private GrowConditionBase<AgriSeason> season;
        private GrowConditionBase<Fluid> fluid;

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
                throw new IllegalStateException("Can not build an IAgriGrowthRequirement without initializing the light level rule");
            }
            if(this.season == null) {
                throw new IllegalStateException("Can not build an IAgriGrowthRequirement without initializing the seasonality rule");
            }
            if(this.fluid == null) {
                throw new IllegalStateException("Can not build an IAgriGrowthRequirement without initializing the fluid rule");
            }
            return new AgriGrowthRequirement(
                    this.conditions, this.humidity, this.acidity, this.nutrients, this.lightLevel, this.season, this.fluid
            );
        }

        @Override
        public IAgriGrowthRequirement.Builder defineHumidity(BiFunction<Integer, IAgriSoil.Humidity, IAgriGrowthResponse> response) {
            this.humidity = this.soilHumidity(response, Tooltips.HUMIDITY_DESCRIPTION);
            return this;
        }

        @Override
        public IAgriGrowthRequirement.Builder defineAcidity(BiFunction<Integer, IAgriSoil.Acidity, IAgriGrowthResponse> response) {
            this.acidity = this.soilAcidity(response, Tooltips.ACIDITY_DESCRIPTION);
            return this;
        }

        @Override
        public IAgriGrowthRequirement.Builder defineNutrients(BiFunction<Integer, IAgriSoil.Nutrients, IAgriGrowthResponse> response) {
            this.nutrients = this.soilNutrients(response, Tooltips.NUTRIENT_DESCRIPTION);
            return this;
        }

        @Override
        public IAgriGrowthRequirement.Builder defineLightLevel(BiFunction<Integer, Integer, IAgriGrowthResponse> response) {
            this.lightLevel = this.light(response, Tooltips.LIGHT_LEVEL_DESCRIPTION);
            return this;
        }

        @Override
        public IAgriGrowthRequirement.Builder defineFluid(BiFunction<Integer, Fluid, IAgriGrowthResponse> response) {
            this.fluid = this.fluid(response, Tooltips.FLUID_DESCRIPTION);
            return this;
        }

        @Override
        public IAgriGrowthRequirement.Builder defineSeasonality(BiFunction<Integer, AgriSeason, IAgriGrowthResponse> response) {
            this.season = this.season(response, Tooltips.SEASON_DESCRIPTION);
            return this;
        }

        @Override
        public IAgriGrowthRequirement.Builder addCondition(IAgriGrowCondition condition) {
            this.conditions.add(condition);
            return this;
        }
    }

    public static final class Tooltips {
        public static final List<Component> HUMIDITY_DESCRIPTION = ImmutableList.of(new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.soil.humidity.general"));
        public static final List<Component> ACIDITY_DESCRIPTION = ImmutableList.of(new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.soil.acidity.general"));
        public static final List<Component> NUTRIENT_DESCRIPTION = ImmutableList.of(new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.soil.nutrients.general"));
        public static final List<Component> LIGHT_LEVEL_DESCRIPTION = ImmutableList.of(new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.light.general"));
        public static final List<Component> FLUID_DESCRIPTION = ImmutableList.of(new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.fluid.general"));
        public static final List<Component> SEASON_DESCRIPTION = ImmutableList.of(new TranslatableComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.season.general"));

        private Tooltips() {}
    }

    private static final IAgriGrowthRequirement NONE = new IAgriGrowthRequirement() {
        @Nonnull
        @Override
        public Set<IAgriGrowCondition> getGrowConditions() {
            return ImmutableSet.of();
        }

        @Override
        public IAgriGrowthResponse getSoilHumidityResponse(IAgriSoil.Humidity humidity, int strength) {
            return IAgriGrowthResponse.INFERTILE;
        }

        @Override
        public IAgriGrowthResponse getSoilAcidityResponse(IAgriSoil.Acidity acidity, int strength) {
            return IAgriGrowthResponse.INFERTILE;
        }

        @Override
        public IAgriGrowthResponse getSoilNutrientsResponse(IAgriSoil.Nutrients nutrients, int strength) {
            return IAgriGrowthResponse.INFERTILE;
        }

        @Override
        public IAgriGrowthResponse getLightLevelResponse(int light, int strength) {
            return IAgriGrowthResponse.INFERTILE;
        }

        @Override
        public IAgriGrowthResponse getSeasonResponse(AgriSeason season, int strength) {
            return IAgriGrowthResponse.INFERTILE;
        }

        @Override
        public IAgriGrowthResponse getFluidResponse(Fluid fluid, int strength) {
            return IAgriGrowthResponse.INFERTILE;
        }
    };
}
