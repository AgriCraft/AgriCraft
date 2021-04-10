package com.infinityraider.agricraft.plugins.sereneseasons;

import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class SereneSeasonsSeasonGetter implements BiFunction<World, BlockPos, AgriSeason> {
    private static final SereneSeasonsSeasonGetter INSTANCE = new SereneSeasonsSeasonGetter();

    public static SereneSeasonsSeasonGetter getInstance() {
        return INSTANCE;
    }

    private SereneSeasonsSeasonGetter() {}

    @Nonnull
    public AgriSeason convert(@Nullable Object obj) {
        if(obj instanceof Season) {
            switch ((Season) obj) {
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
        return this.convert(SeasonHelper.getSeasonState(world).getSeason());
    }
}
