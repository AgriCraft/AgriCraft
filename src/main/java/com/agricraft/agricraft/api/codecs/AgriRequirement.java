package com.agricraft.agricraft.api.codecs;

import com.agricraft.agricraft.api.requirement.AgriSeason;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record AgriRequirement(AgriSoilCondition<AgriSoilCondition.Humidity> soilHumidity,
                              AgriSoilCondition<AgriSoilCondition.Acidity> soilAcidity,
                              AgriSoilCondition<AgriSoilCondition.Nutrients> soilNutrients, int minLight, int maxLight,
                              double lightToleranceFactor, AgriListCondition biomes, AgriListCondition dimensions,
                              List<AgriSeason> seasons, List<AgriBlockCondition> blockConditions,
                              AgriFluidCondition fluidCondition) {

	public static final Codec<AgriRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			AgriSoilCondition.codecForProperty(AgriSoilCondition.Humidity.CODEC).fieldOf("soil_humidity").forGetter(requirement -> requirement.soilHumidity),
			AgriSoilCondition.codecForProperty(AgriSoilCondition.Acidity.CODEC).fieldOf("soil_acidity").forGetter(requirement -> requirement.soilAcidity),
			AgriSoilCondition.codecForProperty(AgriSoilCondition.Nutrients.CODEC).fieldOf("soil_nutrients").forGetter(requirement -> requirement.soilNutrients),
			Codec.INT.fieldOf("min_light").forGetter(requirement -> requirement.minLight),
			Codec.INT.fieldOf("max_light").forGetter(requirement -> requirement.maxLight),
			Codec.DOUBLE.fieldOf("light_tolerance_factor").forGetter(requirement -> requirement.lightToleranceFactor),
			AgriListCondition.CODEC.optionalFieldOf("biomes", AgriListCondition.EMPTY).forGetter(requirement -> requirement.biomes),
			AgriListCondition.CODEC.optionalFieldOf("dimensions", AgriListCondition.EMPTY).forGetter(requirement -> requirement.dimensions),
			AgriSeason.CODEC.listOf().optionalFieldOf("seasons", List.of()).forGetter(requirement -> requirement.seasons),
			AgriBlockCondition.CODEC.listOf().optionalFieldOf("block_conditions", List.of()).forGetter(requirement -> requirement.blockConditions),
			AgriFluidCondition.CODEC.optionalFieldOf("fluid_condition", AgriFluidCondition.EMPTY).forGetter(requirement -> requirement.fluidCondition)
	).apply(instance, AgriRequirement::new));

	public static final AgriRequirement NO_REQUIREMENT = AgriRequirement.builder()
			.humidity(AgriSoilCondition.Humidity.WET, AgriSoilCondition.Type.EQUAL, 0)
			.acidity(AgriSoilCondition.Acidity.NEUTRAL, AgriSoilCondition.Type.EQUAL, 0)
			.nutrients(AgriSoilCondition.Nutrients.MEDIUM, AgriSoilCondition.Type.EQUAL, 0)
			.light(0, 0, 0)
			.seasons()
			.build();

	public AgriRequirement(AgriSoilCondition<AgriSoilCondition.Humidity> soilHumidity, AgriSoilCondition<AgriSoilCondition.Acidity> soilAcidity, AgriSoilCondition<AgriSoilCondition.Nutrients> soilNutrients,
	                       int minLight, int maxLight, double lightToleranceFactor, Optional<AgriListCondition> biomes,
	                       Optional<AgriListCondition> dimensions, Optional<List<AgriSeason>> seasons, Optional<List<AgriBlockCondition>> conditions, Optional<AgriFluidCondition> fluid) {
		this(soilHumidity, soilAcidity, soilNutrients, minLight, maxLight, lightToleranceFactor, biomes.orElse(AgriListCondition.EMPTY), dimensions.orElse(AgriListCondition.EMPTY), seasons.orElse(List.of(AgriSeason.SPRING, AgriSeason.SUMMER, AgriSeason.AUTUMN, AgriSeason.WINTER)), conditions.orElse(List.of()), fluid.orElse(AgriFluidCondition.EMPTY));

	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		AgriSoilCondition<AgriSoilCondition.Humidity> humidity = new AgriSoilCondition<>(AgriSoilCondition.Humidity.WET, AgriSoilCondition.Type.EQUAL, 0.2);
		AgriSoilCondition<AgriSoilCondition.Acidity> acidity = new AgriSoilCondition<>(AgriSoilCondition.Acidity.NEUTRAL, AgriSoilCondition.Type.EQUAL, 0.2);
		AgriSoilCondition<AgriSoilCondition.Nutrients> nutrients = new AgriSoilCondition<>(AgriSoilCondition.Nutrients.MEDIUM, AgriSoilCondition.Type.EQUAL, 0.2);
		int minLight = 10;
		int maxLight = 16;
		double lightToleranceFactor = 0.5;
		AgriListCondition biomes = AgriListCondition.EMPTY;
		AgriListCondition dimensions = AgriListCondition.EMPTY;
		List<AgriSeason> seasons = List.of(AgriSeason.SPRING, AgriSeason.SUMMER, AgriSeason.AUTUMN, AgriSeason.WINTER);
		List<AgriBlockCondition> blockConditions = new ArrayList<>();
		AgriFluidCondition fluidCondition = AgriFluidCondition.EMPTY;

		public static Builder from(AgriRequirement requirement) {
			return new Builder()
					.humidity(requirement.soilHumidity.value(), requirement.soilHumidity.type(), requirement.soilHumidity.toleranceFactor())
					.acidity(requirement.soilAcidity.value(), requirement.soilAcidity.type(), requirement.soilAcidity.toleranceFactor())
					.nutrients(requirement.soilNutrients.value(), requirement.soilNutrients.type(), requirement.soilNutrients.toleranceFactor())
					.light(requirement.minLight, requirement.maxLight, requirement.lightToleranceFactor)
					.biomes(requirement.biomes.ignoreFromStrength(), requirement.biomes.blacklist(), requirement.biomes.values().toArray(new ResourceLocation[0]))
					.dimensions(requirement.dimensions.ignoreFromStrength(), requirement.dimensions.blacklist(), requirement.dimensions.values().toArray(new ResourceLocation[0]))
					.seasons(requirement.seasons().toArray(new AgriSeason[0]))
					.blocks(requirement.blockConditions.toArray(new AgriBlockCondition[0]))
					.fluid(requirement.fluidCondition);
		}

		public AgriRequirement build() {
			return new AgriRequirement(humidity, acidity, nutrients, minLight, maxLight, lightToleranceFactor, biomes, dimensions, seasons, blockConditions, fluidCondition);
		}

		public Builder humidity(AgriSoilCondition.Humidity condition, AgriSoilCondition.Type type, double toleranceFactor) {
			this.humidity = new AgriSoilCondition<>(condition, type, toleranceFactor);
			return this;
		}

		public Builder acidity(AgriSoilCondition.Acidity condition, AgriSoilCondition.Type type, double toleranceFactor) {
			this.acidity = new AgriSoilCondition<>(condition, type, toleranceFactor);
			return this;
		}

		public Builder nutrients(AgriSoilCondition.Nutrients condition, AgriSoilCondition.Type type, double toleranceFactor) {
			this.nutrients = new AgriSoilCondition<>(condition, type, toleranceFactor);
			return this;
		}

		public Builder light(int min, int max, double toleranceFactor) {
			this.minLight = min;
			this.maxLight = max;
			this.lightToleranceFactor = toleranceFactor;
			return this;
		}

		public Builder biomes(ResourceLocation... biomes) {
			this.biomes = new AgriListCondition(List.of(biomes), false, -1);
			return this;
		}

		public Builder biomes(boolean blacklist, ResourceLocation... biomes) {
			this.biomes = new AgriListCondition(List.of(biomes), blacklist, -1);
			return this;
		}

		public Builder biomes(int ignoreFromStrength, boolean blacklist, ResourceLocation... biomes) {
			this.biomes = new AgriListCondition(List.of(biomes), blacklist, ignoreFromStrength);
			return this;
		}

		public Builder dimensions(ResourceLocation... dimensions) {
			this.dimensions = new AgriListCondition(List.of(dimensions), false, -1);
			return this;
		}

		public Builder dimensions(boolean blacklist, ResourceLocation... dimensions) {
			this.dimensions = new AgriListCondition(List.of(dimensions), blacklist, -1);
			return this;
		}

		public Builder dimensions(int ignoreFromStrength, boolean blacklist, ResourceLocation... dimensions) {
			this.dimensions = new AgriListCondition(List.of(dimensions), blacklist, ignoreFromStrength);
			return this;
		}

		public Builder seasons(AgriSeason... seasons) {
			this.seasons = List.of(seasons);
			return this;
		}

		public Builder blocks(AgriBlockCondition... blocks) {
			Collections.addAll(this.blockConditions, blocks);
			return this;
		}

		public Builder clearBlocks() {
			this.blockConditions.clear();
			return this;
		}

		public Builder fluid(AgriFluidCondition fluid) {
			this.fluidCondition = fluid;
			return this;
		}

	}

}