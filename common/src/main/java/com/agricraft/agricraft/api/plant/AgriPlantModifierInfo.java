package com.agricraft.agricraft.api.plant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record AgriPlantModifierInfo(String id, String value) {

	public static final Codec<AgriPlantModifierInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("id").forGetter(callback -> callback.id),
			Codec.STRING.optionalFieldOf("value").forGetter(callback -> callback.value.isEmpty() ? Optional.empty() : Optional.of(callback.value))
	).apply(instance, AgriPlantModifierInfo::new));

	public AgriPlantModifierInfo(String id, Optional<String> value) {
		this(id, value.orElse(""));
	}

	public AgriPlantModifierInfo(String id) {
		this(id, "");
	}

}
