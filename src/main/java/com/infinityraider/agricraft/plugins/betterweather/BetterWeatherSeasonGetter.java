package com.infinityraider.agricraft.plugins.betterweather;

import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import corgitaco.betterweather.api.Climate;
import corgitaco.betterweather.api.season.Season;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class BetterWeatherSeasonGetter implements BiFunction<World, BlockPos, AgriSeason> {
    private static final BetterWeatherSeasonGetter INSTANCE = new BetterWeatherSeasonGetter();

    public static BetterWeatherSeasonGetter getInstance() {
        return INSTANCE;
    }

    private BetterWeatherSeasonGetter() {}

    @Nonnull
    public AgriSeason convert(@Nullable Object obj) {
        if(obj instanceof Season) {
            switch (((Season) obj).getKey()) {
                case SPRING: return AgriSeason.SPRING;
                case SUMMER: return AgriSeason.SUMMER;
                case AUTUMN: return AgriSeason.AUTUMN;
                case WINTER: return  AgriSeason.WINTER;
            }
        }
        return AgriSeason.ANY;
    }

    @Override
    public AgriSeason apply(World world, BlockPos pos) {
        return this.convert(Climate.getSeason(world));
    }
}
