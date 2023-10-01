package com.agricraft.agricraft.api.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record AgriFluidCondition(ExtraCodecs.TagOrElementLocation fluid, List<String> states) {

	public static final Codec<AgriFluidCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.TAG_OR_ELEMENT_ID.fieldOf("fluid").forGetter(stateCondition -> stateCondition.fluid),
			Codec.STRING.listOf().comapFlatMap(AgriFluidCondition::readStates, list -> list).optionalFieldOf("states").forGetter(stateCondition -> stateCondition.states.isEmpty() ? Optional.empty() : Optional.of(stateCondition.states))
	).apply(instance, AgriFluidCondition::new));

	public static final AgriFluidCondition EMPTY = new AgriFluidCondition(new ExtraCodecs.TagOrElementLocation(new ResourceLocation("minecraft", "empty"), false), new ArrayList<>());

	public AgriFluidCondition(ExtraCodecs.TagOrElementLocation block, Optional<List<String>> states) {
		this(block, states.orElse(new ArrayList<>()));
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

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		ExtraCodecs.TagOrElementLocation item = new ExtraCodecs.TagOrElementLocation(new ResourceLocation("minecraft", "air"), false);
		List<String> states = new ArrayList<>();

		public AgriFluidCondition build() {
			return new AgriFluidCondition(item, states);
		}

		public Builder fluid(String location) {
			this.item = new ExtraCodecs.TagOrElementLocation(new ResourceLocation(location), false);
			return this;
		}

		public Builder fluid(String namespace, String path) {
			this.item = new ExtraCodecs.TagOrElementLocation(new ResourceLocation(namespace, path), false);
			return this;
		}

		public Builder tag(String location) {
			this.item = new ExtraCodecs.TagOrElementLocation(new ResourceLocation(location), true);
			return this;
		}

		public Builder tag(String namespace, String path) {
			this.item = new ExtraCodecs.TagOrElementLocation(new ResourceLocation(namespace, path), true);
			return this;
		}

		public Builder states(String... states) {
			Collections.addAll(this.states, states);
			return this;
		}

	}

}
