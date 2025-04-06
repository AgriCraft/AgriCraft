package com.agricraft.agricraft.api.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record AgriParticleEffect(String particle, double deltaX, double deltaY, double deltaZ, double probability,
                                 List<Integer> stages) {

	public static final Codec<AgriParticleEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("particle").forGetter(effect -> effect.particle),
			Codec.DOUBLE.fieldOf("delta_x").forGetter(effect -> effect.deltaX),
			Codec.DOUBLE.fieldOf("delta_y").forGetter(effect -> effect.deltaY),
			Codec.DOUBLE.fieldOf("delta_z").forGetter(effect -> effect.deltaZ),
			Codec.DOUBLE.fieldOf("probability").forGetter(effect -> effect.probability),
			Codec.INT.listOf().fieldOf("stages").forGetter(effect -> effect.stages)
	).apply(instance, AgriParticleEffect::new));

	public boolean allowParticles(int index) {
		return stages.stream().anyMatch(val -> val == index);
	}

}
