package com.agricraft.agricraft.common.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record AgriFluidCondition(ExtraCodecs.TagOrElementLocation fluid, CompoundTag nbt, List<String> states) {

	public static final Codec<AgriFluidCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.TAG_OR_ELEMENT_ID.fieldOf("fluidCondition").forGetter(stateCondition -> stateCondition.fluid),
			CompoundTag.CODEC.optionalFieldOf("nbt").forGetter(stateCondition -> stateCondition.nbt.isEmpty() ? Optional.empty() : Optional.of(stateCondition.nbt)),
			Codec.STRING.listOf().comapFlatMap(AgriFluidCondition::readStates, list -> list).optionalFieldOf("states").forGetter(stateCondition -> stateCondition.states.isEmpty() ? Optional.empty() : Optional.of(stateCondition.states))
	).apply(instance, AgriFluidCondition::new));

	public static final AgriFluidCondition EMPTY = new AgriFluidCondition(new ExtraCodecs.TagOrElementLocation(new ResourceLocation("minecraft", "empty"), false), new CompoundTag(), new ArrayList<>());

	public AgriFluidCondition(ExtraCodecs.TagOrElementLocation block, Optional<CompoundTag> nbt, Optional<List<String>> states) {
		this(block, nbt.orElse(new CompoundTag()), states.orElse(new ArrayList<>()));
	}

	private static DataResult<List<String>> readStates(List<String> states) {
		for (String state : states) {
			if (!state.contains("=") || state.charAt(0) == '=' || state.charAt(state.length() - 1) == '=') {
				return DataResult.error(() -> "invalid state definition");
			}
		}
		return DataResult.success(states);
	}

	public boolean isEmpty() {
		return this == EMPTY;
	}

}
