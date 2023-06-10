package com.agricraft.agricraft.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record AgriListCondition(List<String> values, boolean blacklist, int ignoreFromStrength) {

	public static final Codec<AgriListCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.listOf().fieldOf("values").forGetter(listCondition -> listCondition.values),
			Codec.BOOL.fieldOf("blacklist").forGetter(listCondition -> listCondition.blacklist),
			Codec.INT.fieldOf("ignore_from_strength").forGetter(listCondition -> listCondition.ignoreFromStrength)
	).apply(instance, AgriListCondition::new));

}
