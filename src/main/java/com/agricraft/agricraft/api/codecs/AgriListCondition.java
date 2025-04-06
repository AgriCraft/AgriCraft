package com.agricraft.agricraft.api.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public record AgriListCondition(List<ResourceLocation> values, boolean blacklist, int ignoreFromStrength) {

	public static final Codec<AgriListCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.listOf().fieldOf("values").forGetter(listCondition -> listCondition.values),
			Codec.BOOL.fieldOf("blacklist").forGetter(listCondition -> listCondition.blacklist),
			Codec.INT.optionalFieldOf("ignore_from_strength", -1).forGetter(listCondition -> listCondition.ignoreFromStrength)
	).apply(instance, AgriListCondition::new));

	public static final AgriListCondition EMPTY = new AgriListCondition(List.of(), true, 11);

	public AgriListCondition(List<ResourceLocation> values, boolean blacklist, Optional<Integer> ignoreFromStrength) {
		this(values, blacklist, ignoreFromStrength.orElse(11));
	}

	public boolean isEmpty() {
		return this == EMPTY;
	}

	public boolean accept(ResourceLocation value) {
		if (this.values.contains(value)) {
			return !this.blacklist;
		}
		return this.blacklist;
	}

}
