package com.agricraft.agricraft.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Optional;

public record AgriListCondition(List<String> values, boolean blacklist, int ignoreFromStrength) {

	public static final Codec<AgriListCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.listOf().fieldOf("values").forGetter(listCondition -> listCondition.values),
			Codec.BOOL.fieldOf("blacklist").forGetter(listCondition -> listCondition.blacklist),
			Codec.INT.optionalFieldOf("ignore_from_strength").forGetter(listCondition -> listCondition.ignoreFromStrength == -1 ? Optional.empty() : Optional.of(listCondition.ignoreFromStrength))
	).apply(instance, AgriListCondition::new));

	public static final AgriListCondition EMPTY = new AgriListCondition(List.of(), true, -1);

	public AgriListCondition(List<String> values, boolean blacklist, Optional<Integer> ignoreFromStrength) {
		this(values, blacklist, ignoreFromStrength.orElse(-1));
	}

	public boolean isEmpty() {
		return this == EMPTY;
	}

}
