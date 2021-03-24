package com.infinityraider.agricraft.plugins.betterweather;

import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import corgitaco.betterweather.api.Climate;
import corgitaco.betterweather.api.season.Season;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiFunction;

public class BetterWeatherSeasonGetter implements BiFunction<World, BlockPos, Optional<AgriSeason>> {
    private static final BetterWeatherSeasonGetter INSTANCE = new BetterWeatherSeasonGetter();

    public static BetterWeatherSeasonGetter getInstance() {
        return INSTANCE;
    }

    private BetterWeatherSeasonGetter() {}

    @Nonnull
    public Optional<AgriSeason> convert(@Nullable Object obj) {
        if(obj instanceof Season) {
            switch (((Season) obj).getKey()) {
                case SPRING: return Optional.of(AgriSeason.SPRING);
                case SUMMER: return Optional.of(AgriSeason.SUMMER);
                case AUTUMN: return Optional.of(AgriSeason.AUTUMN);
                case WINTER: return Optional.of(AgriSeason.WINTER);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<AgriSeason> apply(World world, BlockPos pos) {
        return this.convert(Climate.getSeason(world));
    }
}
