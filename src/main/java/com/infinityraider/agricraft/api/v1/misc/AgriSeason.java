package com.infinityraider.agricraft.api.v1.misc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Enum to refer to seasonality for AgriCraft plants
 *
 * Only used for compatibility with mods introducing seasons
 */
public enum AgriSeason {
    SPRING("spring"),
    SUMMER("summer"),
    AUTUMN("autumn", "fall"),
    WINTER("winter");

    private final List<String> keys;

    AgriSeason(String... keys) {
        this.keys = Arrays.asList(keys);
    }

    public static Optional<AgriSeason> fromString(String string) {
        return Arrays.stream(values())
                .filter(season -> season.keys.stream().anyMatch(key -> key.equalsIgnoreCase(string)))
                .findAny();
    }
}
