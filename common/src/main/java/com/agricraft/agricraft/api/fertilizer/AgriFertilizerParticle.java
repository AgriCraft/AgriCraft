package com.agricraft.agricraft.api.fertilizer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record AgriFertilizerParticle(String particle, double deltaX, double deltaY, double deltaZ, int amount,
                                     List<String> when) {

	public static final Codec<AgriFertilizerParticle> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("particle").forGetter(effect -> effect.particle),
			Codec.DOUBLE.fieldOf("delta_x").forGetter(effect -> effect.deltaX),
			Codec.DOUBLE.fieldOf("delta_y").forGetter(effect -> effect.deltaY),
			Codec.DOUBLE.fieldOf("delta_z").forGetter(effect -> effect.deltaZ),
			Codec.INT.fieldOf("amount").forGetter(effect -> effect.amount),
			Codec.STRING.listOf().fieldOf("when").forGetter(effect -> effect.when)
	).apply(instance, AgriFertilizerParticle::new));

}
