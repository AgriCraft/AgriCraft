package com.agricraft.agricraft.api.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record AgriPlantCallback(String id, String value) {

	public static final Codec<AgriPlantCallback> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("id").forGetter(callback -> callback.id),
			Codec.STRING.optionalFieldOf("value").forGetter(callback -> callback.value.isEmpty() ? Optional.empty() : Optional.of(callback.value))
	).apply(instance, AgriPlantCallback::new));

	public AgriPlantCallback(String id, Optional<String> value) {
		this(id, value.orElse(""));
	}

}
