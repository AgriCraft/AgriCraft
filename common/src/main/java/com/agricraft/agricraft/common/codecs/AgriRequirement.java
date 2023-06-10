package com.agricraft.agricraft.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record AgriRequirement(AgriSoilCondition soilHumidity, AgriSoilCondition soilAcidity,
                              AgriSoilCondition soilNutrients, int minLight, int maxLight,
                              double lightToleranceFactor, AgriListCondition biomes, AgriListCondition dimensions,
                              List<String> seasons, List<AgriBlockCondition> conditions, AgriFluidCondition fluid) {

	public static final Codec<AgriRequirement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			AgriSoilCondition.CODEC.fieldOf("soil_humidity").forGetter(requirement -> requirement.soilHumidity),
			AgriSoilCondition.CODEC.fieldOf("soil_acidity").forGetter(requirement -> requirement.soilAcidity),
			AgriSoilCondition.CODEC.fieldOf("soil_nutrients").forGetter(requirement -> requirement.soilNutrients),
			Codec.INT.fieldOf("min_light").forGetter(requirement -> requirement.minLight),
			Codec.INT.fieldOf("max_light").forGetter(requirement -> requirement.maxLight),
			Codec.DOUBLE.fieldOf("light_tolerance_factor").forGetter(requirement -> requirement.lightToleranceFactor),
			AgriListCondition.CODEC.fieldOf("biomes").forGetter(requirement -> requirement.biomes),
			AgriListCondition.CODEC.fieldOf("dimensions").forGetter(requirement -> requirement.dimensions),
			Codec.STRING.listOf().fieldOf("seasons").forGetter(requirement -> requirement.seasons),
			AgriBlockCondition.CODEC.listOf().fieldOf("conditions").forGetter(requirement -> requirement.conditions),
			AgriFluidCondition.CODEC.fieldOf("fluid").forGetter(requirement -> requirement.fluid)
	).apply(instance, AgriRequirement::new));

	public record AgriSoilCondition(String condition, String type, double toleranceFactor) {

		public static final Codec<AgriSoilCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("condition").forGetter(soilCondition -> soilCondition.condition),
				Codec.STRING.fieldOf("type").forGetter(soilCondition -> soilCondition.type),
				Codec.DOUBLE.fieldOf("tolerance_factor").forGetter(soilCondition -> soilCondition.toleranceFactor)
		).apply(instance, AgriSoilCondition::new));

	}

}
