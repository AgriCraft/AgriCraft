package com.infinityraider.agricraft.plugins.simplyseasons;

import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import uk.joshiejack.simplyseasons.api.SSeasonsAPI;
import uk.joshiejack.simplyseasons.api.Season;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class SimplySeasonsSeasonGetter implements BiFunction<World, BlockPos, AgriSeason> {
    private static final SimplySeasonsSeasonGetter INSTANCE = new SimplySeasonsSeasonGetter();

    public static SimplySeasonsSeasonGetter getInstance() {
        return INSTANCE;
    }

    private SimplySeasonsSeasonGetter() {}

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
        return world.getCapability(SSeasonsAPI.SEASONS_CAPABILITY)
                .map(provider -> provider.getSeason(world))
                .map(this::convert)
                .orElse(AgriSeason.ANY);
    }
}
