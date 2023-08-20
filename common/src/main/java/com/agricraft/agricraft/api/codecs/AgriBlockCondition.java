package com.agricraft.agricraft.api.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Optional;

public record AgriBlockCondition(ExtraCodecs.TagOrElementLocation block, CompoundTag nbt, List<String> states,
                                 int strength, int amount, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {

	public static final Codec<AgriBlockCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.TAG_OR_ELEMENT_ID.fieldOf("block").forGetter(blockCondition -> blockCondition.block),
			CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(blockCondition -> blockCondition.nbt.isEmpty() ? Optional.empty() : Optional.of(blockCondition.nbt)),
			Codec.STRING.listOf().comapFlatMap(AgriBlockCondition::readStates, list -> list).optionalFieldOf("states").forGetter(blockCondition -> blockCondition.states.isEmpty() ? Optional.empty() : Optional.of(blockCondition.states)),
			Codec.INT.fieldOf("strength").forGetter(blockCondition -> blockCondition.strength),
			Codec.INT.fieldOf("amount").forGetter(blockCondition -> blockCondition.amount),
			Codec.INT.fieldOf("min_x").forGetter(blockCondition -> blockCondition.minX),
			Codec.INT.fieldOf("min_y").forGetter(blockCondition -> blockCondition.minY),
			Codec.INT.fieldOf("min_z").forGetter(blockCondition -> blockCondition.minZ),
			Codec.INT.fieldOf("max_x").forGetter(blockCondition -> blockCondition.maxX),
			Codec.INT.fieldOf("max_y").forGetter(blockCondition -> blockCondition.maxY),
			Codec.INT.fieldOf("max_z").forGetter(blockCondition -> blockCondition.maxZ)
	).apply(instance, AgriBlockCondition::new));

	public AgriBlockCondition(ExtraCodecs.TagOrElementLocation block, Optional<CompoundTag> nbt, Optional<List<String>> states, int strength, int amount, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		this(block, nbt.orElse(new CompoundTag()), states.orElse(List.of()), strength, amount, minX, minY, minZ, maxX, maxY, maxZ);
	}

	private static DataResult<List<String>> readStates(List<String> states) {
		for (String state : states) {
			if (!state.contains("=") || state.charAt(0) == '=' || state.charAt(state.length() - 1) == '=') {
				return DataResult.error(() -> "invalid state definition");
			}
		}
		return DataResult.success(states);
	}

}
