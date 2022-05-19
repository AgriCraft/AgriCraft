package com.infinityraider.agricraft.plugins.sereneseasons;

import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractGlassBlock;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.FertilityConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class SereneSeasonsSeasonGetter implements BiFunction<Level, BlockPos, AgriSeason> {
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
    public AgriSeason apply(Level world, BlockPos pos) {
        // Serene Seasons cave stuff
        if (FertilityConfig.undergroundFertilityLevel.get() > -1
                && pos.getY() < FertilityConfig.undergroundFertilityLevel.get()
                && !world.canSeeSky(pos)) {
            return AgriSeason.ANY;
        }
        // Serene Seasons greenhouse stuff
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for(int i = 0; i < 16; ++i) {
            mutablePos.set(pos.getX(), pos.getY() + i, pos.getZ());
            if (world.getBlockState(mutablePos).getBlock() instanceof AbstractGlassBlock) {
                return AgriSeason.ANY;
            }
        }
        // Fall back to default logic
        return this.convert(SeasonHelper.getSeasonState(world).getSeason());
    }
}
