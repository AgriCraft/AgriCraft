package com.agricraft.agricraft.api.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record AgriStructureGenSettings(List<String> structures, int weight, int statsMin, int statsMax) {

	public static final Codec<AgriStructureGenSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.listOf().fieldOf("structures").forGetter(settings -> settings.structures),
			Codec.INT.fieldOf("weight").forGetter(settings -> settings.weight),
			Codec.INT.fieldOf("statsMin").forGetter(settings -> settings.statsMin),
			Codec.INT.fieldOf("statsMax").forGetter(settings -> settings.statsMax)
	).apply(instance, AgriStructureGenSettings::new));

}
