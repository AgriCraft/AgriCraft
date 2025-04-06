package com.agricraft.agricraft.api.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.List;
import java.util.Optional;

public record AgriBlockCondition(ExtraCodecs.TagOrElementLocation block, List<String> states, int strength) {

	public static final Codec<AgriBlockCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.TAG_OR_ELEMENT_ID.fieldOf("block").forGetter(blockCondition -> blockCondition.block),
			Codec.STRING.listOf().comapFlatMap(AgriBlockCondition::readStates, list -> list).optionalFieldOf("states", List.of()).forGetter(blockCondition -> blockCondition.states),
			Codec.INT.fieldOf("strength").forGetter(blockCondition -> blockCondition.strength)
	).apply(instance, AgriBlockCondition::new));

	public AgriBlockCondition(ExtraCodecs.TagOrElementLocation block, Optional<List<String>> states, int strength) {
		this(block, states.orElse(List.of()), strength);
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
		ExtraCodecs.TagOrElementLocation item = new ExtraCodecs.TagOrElementLocation(ResourceLocation.withDefaultNamespace("air"), false);
		List<String> states = List.of();
		int strength = 11;

		public AgriBlockCondition build() {
			return new AgriBlockCondition(this.item, this.states, this.strength);
		}

		public Builder item(String location) {
			this.item = new ExtraCodecs.TagOrElementLocation(ResourceLocation.parse(location), false);
			return this;
		}
		public Builder item(String namespace, String path) {
			this.item = new ExtraCodecs.TagOrElementLocation(ResourceLocation.fromNamespaceAndPath(namespace, path), false);
			return this;
		}
		public Builder tag(String location) {
			this.item = new ExtraCodecs.TagOrElementLocation(ResourceLocation.parse(location), true);
			return this;
		}
		public Builder tag(String namespace, String path) {
			this.item = new ExtraCodecs.TagOrElementLocation(ResourceLocation.fromNamespaceAndPath(namespace, path), true);
			return this;
		}
		public Builder states(String... states) {
			this.states = List.of(states);
			return this;
		}
		public Builder strength(int strength) {
			this.strength = strength;
			return this;
		}
	}
}
