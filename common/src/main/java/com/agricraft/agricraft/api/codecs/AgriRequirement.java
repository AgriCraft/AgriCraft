package com.agricraft.agricraft.api.codecs;

import com.agricraft.agricraft.api.requirement.AgriSeason;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

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
			AgriListCondition.CODEC.optionalFieldOf("biomes").forGetter(requirement -> requirement.biomes.isEmpty() ? Optional.empty() : Optional.of(requirement.biomes)),
			AgriListCondition.CODEC.optionalFieldOf("dimensions").forGetter(requirement -> requirement.dimensions.isEmpty() ? Optional.empty() : Optional.of(requirement.dimensions)),
			AgriSeason.CODEC.listOf().optionalFieldOf("seasons").forGetter(requirement -> requirement.seasons.isEmpty() ? Optional.empty() : Optional.of(requirement.seasons)),
			AgriBlockCondition.CODEC.listOf().optionalFieldOf("block_conditions").forGetter(requirement -> requirement.blockConditions.isEmpty() ? Optional.empty() : Optional.of(requirement.blockConditions)),
			AgriFluidCondition.CODEC.optionalFieldOf("fluid_condition").forGetter(requirement -> requirement.fluidCondition.isEmpty() ? Optional.empty() : Optional.of(requirement.fluidCondition))
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
		List<AgriBlockCondition> blockConditions = List.of();
		AgriFluidCondition fluidCondition = AgriFluidCondition.EMPTY;

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

		public Builder biomes(int ingnoreFromStrength, boolean blacklist, ResourceLocation... biomes) {
			this.biomes = new AgriListCondition(List.of(biomes), blacklist, ingnoreFromStrength);
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

		public Builder dimensions(int ingnoreFromStrength, boolean blacklist, ResourceLocation... dimensions) {
			this.dimensions = new AgriListCondition(List.of(dimensions), blacklist, ingnoreFromStrength);
			return this;
		}

		public Builder seasons(AgriSeason... seasons) {
			this.seasons = List.of(seasons);
			return this;
		}

		public Builder blocks(AgriBlockCondition... blocks) {
			this.blockConditions = List.of(blocks);
			return this;
		}

		public Builder fluid(AgriFluidCondition fluid) {
			this.fluidCondition = fluid;
			return this;
		}

	}

}