package com.infinityraider.agricraft.plugins.betterweather;

import com.infinityraider.agricraft.api.v1.plugin.*;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.impl.v1.requirement.SeasonPlugin;
import com.infinityraider.agricraft.reference.Names;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiFunction;

@SuppressWarnings("unused")
@AgriPlugin(modId = Names.Mods.BETTER_WEATHER)
public class BetterWeatherPlugin extends SeasonPlugin implements IAgriPlugin {
    @Override
    public String getId() {
        return Names.Mods.BETTER_WEATHER;
    }

    @Override
    public String getDescription() {
        return "Better Weather season compatibility";
    }

    @Override
    protected BiFunction<World, BlockPos, AgriSeason> getSeasonGetter() {
        return BetterWeatherSeasonGetter.getInstance();
    }
}
