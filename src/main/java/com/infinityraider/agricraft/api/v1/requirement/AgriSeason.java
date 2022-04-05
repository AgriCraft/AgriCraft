package com.infinityraider.agricraft.api.v1.requirement;

import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Enum to refer to seasonality for AgriCraft plants.
 *
 * AgriCraft does not natively introduce seasons itself.
 * This enum is therefore only used for compatibility with mods introducing seasons.
 *
 * Mods can register their season logic with the therefore designed instance of IAgriSeasonLogic,
 * this instance is obtained via AgriApi.getSeasonLogic().
 *
 * If no season mod logic is registered, all season queries will return ANY.
 */
public enum AgriSeason {
    /** Regular seasons */
    SPRING("spring"),
    SUMMER("summer"),
    AUTUMN("autumn", "fall"),
    WINTER("winter"),

    /** Allows for any season, if this season is returned, any crop will be able to grow */
    ANY("any", "all");

    private final List<String> keys;

    AgriSeason(String... keys) {
        this.keys = Arrays.asList(keys);
    }

    /**
     * Checks if a season matches this season
     *
     * @param other the other season
     * @return true if the seasons are equal, or either of them is ANY
     */
    public boolean matches(AgriSeason other) {
        return other == this || other == ANY || this == ANY;
    }

    /**
     * Check if a season is an actual valid season
     *
     * @return true if this is a season, false if this is ANY
     */
    public boolean isSeason() {
        return this != ANY;
    }

    /**
     * @return Text Component for the display name of the season
     */
    public TranslatableComponent getDisplayName() {
        return new TranslatableComponent("agricraft.season." + this.name().toLowerCase());
    }

    /**
     * Fetches the season from the world at the given position
     *
     * @param world the world
     * @param pos the position
     * @return the season
     */
    public static AgriSeason getSeason(Level world, BlockPos pos) {
        return AgriApi.getSeasonLogic().getSeason(world, pos);
    }

    /**
     * Parses a season from a string
     * @param string the string
     * @return an optional holding the season if the string matches any of its keys, or else, empty
     */
    public static Optional<AgriSeason> fromString(String string) {
        return Arrays.stream(values())
                .filter(season -> season.keys.stream().anyMatch(string::equalsIgnoreCase))
                .findAny();
    }

    /**
     * @return a stream of the four seasons
     */
    public static Stream<AgriSeason> stream() {
        return Arrays.stream(values()).filter(season -> season != ANY);
    }
}
