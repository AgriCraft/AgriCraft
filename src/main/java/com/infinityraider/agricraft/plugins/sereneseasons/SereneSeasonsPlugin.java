package com.infinityraider.agricraft.plugins.sereneseasons;

import com.infinityraider.agricraft.api.v1.plugin.*;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.impl.v1.requirement.SeasonPlugin;
import com.infinityraider.agricraft.reference.Names;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiFunction;

@SuppressWarnings("unused")
@AgriPlugin(modId = Names.Mods.SERENE_SEASONS)
public class SereneSeasonsPlugin extends SeasonPlugin implements IAgriPlugin {
    @Override
    public String getId() {
        return Names.Mods.SERENE_SEASONS;
    }

    @Override
    public String getDescription() {
        return "Serene Seasons season compatibility";
    }

    @Override
    protected BiFunction<World, BlockPos, AgriSeason> getSeasonGetter() {
        return SereneSeasonsSeasonGetter.getInstance();
    }
}
