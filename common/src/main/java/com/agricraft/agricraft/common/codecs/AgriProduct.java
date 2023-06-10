package com.agricraft.agricraft.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

public record AgriProduct(ExtraCodecs.TagOrElementLocation item, CompoundTag nbt, int min, int max, double chance,
                          boolean required) {

	public static final Codec<AgriProduct> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.TAG_OR_ELEMENT_ID.fieldOf("item").forGetter(product -> product.item),
			CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(product -> product.nbt.isEmpty() ? Optional.empty() : Optional.of(product.nbt)),
			Codec.INT.fieldOf("min").forGetter(product -> product.min),
			Codec.INT.fieldOf("max").forGetter(product -> product.max),
			Codec.DOUBLE.fieldOf("chance").forGetter(product -> product.chance),
			Codec.BOOL.fieldOf("required").forGetter(product -> product.required)
	).apply(instance, AgriProduct::new));

	public AgriProduct(ExtraCodecs.TagOrElementLocation item, Optional<CompoundTag> nbt, int min, int max, double chance, boolean required) {
		// codec use
		this(item, nbt.orElse(new CompoundTag()), min, max, chance, required);
	}

}
