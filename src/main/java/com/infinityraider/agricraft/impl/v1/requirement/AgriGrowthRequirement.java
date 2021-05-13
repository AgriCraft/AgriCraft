package com.infinityraider.agricraft.impl.v1.requirement;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.requirement.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

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

        @Override
        public boolean isSeasonAccepted(AgriSeason season, int strength) {
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

    private final GrowConditionBase<IAgriSoil.Humidity> humidity;
    private final GrowConditionBase<IAgriSoil.Acidity> acidity;
    private final GrowConditionBase<IAgriSoil.Nutrients> nutrients;
    private final GrowConditionBase<Integer> lightLevel;
    private final GrowConditionBase<AgriSeason> season;

    private AgriGrowthRequirement(Set<IAgriGrowCondition> conditions,
                                  GrowConditionBase<IAgriSoil.Humidity> humidity,
                                  GrowConditionBase<IAgriSoil.Acidity> acidity,
                                  GrowConditionBase<IAgriSoil.Nutrients> nutrients,
                                  GrowConditionBase<Integer> lightLevel,
                                  GrowConditionBase<AgriSeason> season) {
        this.humidity = humidity;
        this.acidity = acidity;
        this.nutrients = nutrients;
        this.lightLevel = lightLevel;
        this.season = season;
        ImmutableSet.Builder<IAgriGrowCondition> builder = ImmutableSet.builder();
        builder.add(this.humidity, this.acidity, this.nutrients, this.lightLevel, this.season);
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
        return this.humidity.test(strength, humidity);
    }

    @Override
    public boolean isSoilAcidityAccepted(IAgriSoil.Acidity acidity, int strength) {
        return this.acidity.test(strength, acidity);
    }

    @Override
    public boolean isSoilNutrientsAccepted(IAgriSoil.Nutrients nutrients, int strength) {
        return this.nutrients.test(strength, nutrients);
    }

    @Override
    public boolean isLightLevelAccepted(int light, int strength) {
        return this.lightLevel.test(strength, light);
    }

    @Override
    public boolean isSeasonAccepted(AgriSeason season, int strength) {
        return this.season.test(strength, season);
    }

    private static class Builder extends Factory implements IAgriGrowthRequirement.Builder {

        private final Set<IAgriGrowCondition> conditions;

        private GrowConditionBase<IAgriSoil.Humidity> humidity;
        private GrowConditionBase<IAgriSoil.Acidity> acidity;
        private GrowConditionBase<IAgriSoil.Nutrients> nutrients;
        private GrowConditionBase<Integer> lightLevel;
        private GrowConditionBase<AgriSeason> season;

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
            return new AgriGrowthRequirement(this.conditions, this.humidity, this.acidity, this.nutrients, this.lightLevel, this.season);
        }

        @Override
        public IAgriGrowthRequirement.Builder defineHumidity(BiPredicate<Integer, IAgriSoil.Humidity> predicate) {
            this.humidity = this.soilHumidity(predicate, Tooltips.HUMIDITY_DESCRIPTION);
            return this;
        }

        @Override
        public IAgriGrowthRequirement.Builder defineAcidity(BiPredicate<Integer, IAgriSoil.Acidity> predicate) {
            this.acidity = this.soilAcidity(predicate, Tooltips.ACIDITY_DESCRIPTION);
            return this;
        }

        @Override
        public IAgriGrowthRequirement.Builder defineNutrients(BiPredicate<Integer, IAgriSoil.Nutrients> predicate) {
            this.nutrients = this.soilNutrients(predicate, Tooltips.NUTRIENT_DESCRIPTION);
            return this;
        }

        @Override
        public IAgriGrowthRequirement.Builder defineLightLevel(BiPredicate<Integer, Integer> predicate) {
            this.lightLevel = this.light(predicate, Tooltips.LIGHT_LEVEL_DESCRIPTION);
            return this;
        }

        @Override
        public IAgriGrowthRequirement.Builder defineSeasonality(BiPredicate<Integer, AgriSeason> predicate) {
            this.season = this.season(predicate, Tooltips.SEASON_DESCRIPTION);
            return this;
        }

        @Override
        public IAgriGrowthRequirement.Builder addCondition(IAgriGrowCondition condition) {
            this.conditions.add(condition);
            return this;
        }
    }

    public static final class Tooltips {
        public static final List<ITextComponent> HUMIDITY_DESCRIPTION = ImmutableList.of(new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.soil.humidity.general"));
        public static final List<ITextComponent> ACIDITY_DESCRIPTION = ImmutableList.of(new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.soil.acidity.general"));
        public static final List<ITextComponent> NUTRIENT_DESCRIPTION = ImmutableList.of(new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.soil.nutrients.general"));
        public static final List<ITextComponent> LIGHT_LEVEL_DESCRIPTION = ImmutableList.of(new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.light.general"));
        public static final List<ITextComponent> SEASON_DESCRIPTION = ImmutableList.of(new TranslationTextComponent(
                AgriCraft.instance.getModId() + ".tooltip.growth_req.season.general"));

        private Tooltips() {}
    }
}
