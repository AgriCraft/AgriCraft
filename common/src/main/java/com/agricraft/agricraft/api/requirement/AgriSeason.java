package com.agricraft.agricraft.api.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum AgriSeason {
	// Regular seasons
	SPRING("spring"),
	SUMMER("summer"),
	AUTUMN("autumn", "fall"),
	WINTER("winter"),
	/**
	 * Allows for any season, if this season is returned, any crop will be able to grow
	 */
	ANY("any", "all");

	public static final Codec<AgriSeason> CODEC = Codec.STRING.comapFlatMap(s -> AgriSeason.fromString(s)
					.map(DataResult::success)
					.orElseGet(() -> DataResult.error(() -> s + " is no a valid season")),
			type -> type.name().toLowerCase());

	private final List<String> keys;

	AgriSeason(String... keys) {
		this.keys = Arrays.asList(keys);
	}

	public static Optional<AgriSeason> fromString(String string) {
		return Arrays.stream(values())
				.filter(season -> season.keys.stream().anyMatch(string::equalsIgnoreCase))
				.findAny();
	}

	public boolean matches(AgriSeason other) {
		return other == this || other == ANY || this == ANY;
	}

}
