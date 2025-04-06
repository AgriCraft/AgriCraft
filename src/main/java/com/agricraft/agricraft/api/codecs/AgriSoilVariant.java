package com.agricraft.agricraft.api.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record AgriSoilVariant(ExtraCodecs.TagOrElementLocation block, List<String> states) {

	public static final Codec<AgriSoilVariant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.TAG_OR_ELEMENT_ID.fieldOf("block").forGetter(variant -> variant.block),
			Codec.STRING.listOf().comapFlatMap(AgriSoilVariant::readStates, list -> list).optionalFieldOf("states", List.of()).forGetter(variant -> variant.states)
	).apply(instance, AgriSoilVariant::new));

	public AgriSoilVariant(ExtraCodecs.TagOrElementLocation block, Optional<List<String>> states) {
		this(block, states.orElse(List.of()));
	}

	private static DataResult<List<String>> readStates(List<String> states) {
		for (String state : states) {
			if (!state.contains("=") || state.charAt(0) == '=' || state.charAt(state.length() - 1) == '=') {
				return DataResult.error(() -> "invalid state definition");
			}
		}
		return DataResult.success(states);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		ExtraCodecs.TagOrElementLocation block = new ExtraCodecs.TagOrElementLocation(ResourceLocation.withDefaultNamespace("air"), false);
		List<String> states = new ArrayList<>();

		public AgriSoilVariant build() {
			return new AgriSoilVariant(block, states);
		}

		public Builder block(String location) {
			this.block = new ExtraCodecs.TagOrElementLocation(ResourceLocation.parse(location), false);
			return this;
		}

		public Builder block(String namespace, String path) {
			this.block = new ExtraCodecs.TagOrElementLocation(ResourceLocation.fromNamespaceAndPath(namespace, path), false);
			return this;
		}

		public Builder tag(String location) {
			this.block = new ExtraCodecs.TagOrElementLocation(ResourceLocation.parse(location), true);
			return this;
		}

		public Builder tag(String namespace, String path) {
			this.block = new ExtraCodecs.TagOrElementLocation(ResourceLocation.fromNamespaceAndPath(namespace, path), true);
			return this;
		}

		public Builder states(String... states) {
			Collections.addAll(this.states, states);
			return this;
		}

	}

}
