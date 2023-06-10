package com.agricraft.agricraft.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

public record AgriSeed(ExtraCodecs.TagOrElementLocation item, boolean overridePlanting, CompoundTag nbt,
                       double grassDropChance, double seedDropChance, double seedDropBonus) {

	public static final Codec<AgriSeed> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.TAG_OR_ELEMENT_ID.fieldOf("item").forGetter(seed -> seed.item),
			Codec.BOOL.fieldOf("override_planting").forGetter(seed -> seed.overridePlanting),
			CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(seed -> seed.nbt.isEmpty() ? Optional.empty() : Optional.of(seed.nbt)),
			Codec.DOUBLE.fieldOf("grass_drop_chance").forGetter(plant -> plant.grassDropChance),
			Codec.DOUBLE.fieldOf("seed_drop_bonus").forGetter(plant -> plant.seedDropBonus),
			Codec.DOUBLE.fieldOf("seed_drop_chance").forGetter(plant -> plant.seedDropChance)
			).apply(instance, AgriSeed::new));

	public AgriSeed(ExtraCodecs.TagOrElementLocation item, boolean overridePlanting, Optional<CompoundTag> nbt,
	                double grassDropChance, double seedDropChance, double seedDropBonus) {
		// codec use
		this(item, overridePlanting, nbt.orElse(new CompoundTag()), grassDropChance, seedDropChance, seedDropBonus);
	}

}
