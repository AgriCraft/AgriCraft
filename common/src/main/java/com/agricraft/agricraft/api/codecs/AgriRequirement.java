package com.agricraft.agricraft.api.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

public record AgriRequirement(AgriSoilCondition soilHumidity, AgriSoilCondition soilAcidity,
                              AgriSoilCondition soilNutrients, int minLight, int maxLight,
                              double lightToleranceFactor, AgriListCondition biomes, AgriListCondition dimensions,
                              List<String> seasons, List<AgriBlockCondition> blockConditions, AgriFluidCondition fluidCondition) {

	public static final Codec<AgriRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			AgriSoilCondition.CODEC.fieldOf("soil_humidity").forGetter(requirement -> requirement.soilHumidity),
			AgriSoilCondition.CODEC.fieldOf("soil_acidity").forGetter(requirement -> requirement.soilAcidity),
			AgriSoilCondition.CODEC.fieldOf("soil_nutrients").forGetter(requirement -> requirement.soilNutrients),
			Codec.INT.fieldOf("min_light").forGetter(requirement -> requirement.minLight),
			Codec.INT.fieldOf("max_light").forGetter(requirement -> requirement.maxLight),
			Codec.DOUBLE.fieldOf("light_tolerance_factor").forGetter(requirement -> requirement.lightToleranceFactor),
			AgriListCondition.CODEC.optionalFieldOf("biomes").forGetter(requirement -> requirement.biomes.isEmpty() ? Optional.empty() : Optional.of(requirement.biomes)),
			AgriListCondition.CODEC.optionalFieldOf("dimensions").forGetter(requirement -> requirement.dimensions.isEmpty() ? Optional.empty() : Optional.of(requirement.dimensions)),
			Codec.STRING.listOf().optionalFieldOf("seasons").forGetter(requirement -> requirement.seasons.isEmpty() ? Optional.empty() : Optional.of(requirement.seasons)),
			AgriBlockCondition.CODEC.listOf().optionalFieldOf("block_conditions").forGetter(requirement -> requirement.blockConditions.isEmpty() ? Optional.empty() : Optional.of(requirement.blockConditions)),
			AgriFluidCondition.CODEC.optionalFieldOf("fluid_conditions").forGetter(requirement -> requirement.fluidCondition.isEmpty() ? Optional.empty() : Optional.of(requirement.fluidCondition))
	).apply(instance, AgriRequirement::new));

	public static final AgriRequirement NO_REQUIREMENT = AgriRequirement.builder()
			.humidity("", "", 0)
			.acidity("", "", 0)
			.nutrients("", "", 0)
			.light(0, 0, 0)
			.seasons()
			.build();

	public AgriRequirement(AgriSoilCondition soilHumidity, AgriSoilCondition soilAcidity, AgriSoilCondition soilNutrients,
	                       int minLight, int maxLight, double lightToleranceFactor, Optional<AgriListCondition> biomes,
	                       Optional<AgriListCondition> dimensions, Optional<List<String>> seasons, Optional<List<AgriBlockCondition>> conditions, Optional<AgriFluidCondition> fluid) {
		this(soilHumidity, soilAcidity, soilNutrients, minLight, maxLight, lightToleranceFactor, biomes.orElse(AgriListCondition.EMPTY), dimensions.orElse(AgriListCondition.EMPTY), seasons.orElse(List.of("spring", "summer", "autumn", "winter")), conditions.orElse(List.of()), fluid.orElse(AgriFluidCondition.EMPTY));

	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		AgriSoilCondition humidity = new AgriSoilCondition("wet", "equal", 0.2);
		AgriSoilCondition acidity = new AgriSoilCondition("neutral", "equal", 0.2);
		AgriSoilCondition nutrients = new AgriSoilCondition("medium", "equal", 0.2);
		int minLight = 10;
		int maxLight = 16;
		double lightToleranceFactor = 0.5;
		AgriListCondition biomes = AgriListCondition.EMPTY;
		AgriListCondition dimensions = AgriListCondition.EMPTY;
		List<String> seasons = List.of("spring", "summer", "autumn", "winter");
		List<AgriBlockCondition> blockConditions = List.of();
		AgriFluidCondition fluidCondition = AgriFluidCondition.EMPTY;

		public AgriRequirement build() {
			return new AgriRequirement(humidity, acidity, nutrients, minLight, maxLight, lightToleranceFactor, biomes, dimensions, seasons, blockConditions, fluidCondition);
		}

		public Builder humidity(String condition, String type, double toleranceFactor) {
			this.humidity = new AgriSoilCondition(condition, type, toleranceFactor);
			return this;
		}
		public Builder acidity(String condition, String type, double toleranceFactor) {
			this.acidity = new AgriSoilCondition(condition, type, toleranceFactor);
			return this;
		}
		public Builder nutrients(String condition, String type, double toleranceFactor) {
			this.nutrients = new AgriSoilCondition(condition, type, toleranceFactor);
			return this;
		}
		public Builder light(int min, int max, double toleranceFactor) {
			this.minLight = min;
			this.maxLight = max;
			this.lightToleranceFactor = toleranceFactor;
			return this;
		}
		public Builder biomes(String... biomes) {
			this.biomes = new AgriListCondition(List.of(biomes), false, -1);
			return this;
		}
		public Builder biomes(boolean blacklist, String... biomes) {
			this.biomes = new AgriListCondition(List.of(biomes), blacklist, -1);
			return this;
		}
		public Builder biomes(int ingnoreFromStrength, boolean blacklist, String... biomes) {
			this.biomes = new AgriListCondition(List.of(biomes), blacklist, ingnoreFromStrength);
			return this;
		}
		public Builder dimensions(String... dimensions) {
			this.dimensions = new AgriListCondition(List.of(dimensions), false, -1);
			return this;
		}
		public Builder dimensions(boolean blacklist, String... dimensions) {
			this.dimensions = new AgriListCondition(List.of(dimensions), blacklist, -1);
			return this;
		}
		public Builder dimensions(int ingnoreFromStrength, boolean blacklist, String... dimensions) {
			this.dimensions = new AgriListCondition(List.of(dimensions), blacklist, ingnoreFromStrength);
			return this;
		}
		public Builder seasons(String... seasons) {
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


	public record AgriSoilCondition(String condition, String type, double toleranceFactor) {

		public static final Codec<AgriSoilCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("condition").forGetter(soilCondition -> soilCondition.condition),
				Codec.STRING.fieldOf("type").forGetter(soilCondition -> soilCondition.type),
				Codec.DOUBLE.fieldOf("tolerance_factor").forGetter(soilCondition -> soilCondition.toleranceFactor)
		).apply(instance, AgriSoilCondition::new));

	}

}
