package com.infinityraider.agricraft.plugins.simplyseasons;

import com.infinityraider.agricraft.api.v1.plugin.*;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.impl.v1.requirement.SeasonPlugin;
import com.infinityraider.agricraft.reference.Names;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiFunction;

@SuppressWarnings("unused")
@AgriPlugin(modId = Names.Mods.SIMPLY_SEASONS)
public class SimplySeasonsPlugin extends SeasonPlugin implements IAgriPlugin {
    @Override
    public String getId() {
        return Names.Mods.SIMPLY_SEASONS;
    }

    @Override
    public String getDescription() {
        return "Simply Seasons season compatibility";
    }

    @Override
    protected BiFunction<World, BlockPos, AgriSeason> getSeasonGetter() {
        return SimplySeasonsSeasonGetter.getInstance();
    }
}
