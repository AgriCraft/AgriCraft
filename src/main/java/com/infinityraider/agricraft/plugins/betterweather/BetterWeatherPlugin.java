package com.infinityraider.agricraft.plugins.betterweather;

import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSeasonLogic;
import com.infinityraider.agricraft.impl.v1.requirement.SeasonLogic;
import com.infinityraider.agricraft.reference.Names;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;

@AgriPlugin
@SuppressWarnings("unused")
public class BetterWeatherPlugin implements IAgriPlugin {
    @Override
    public boolean isEnabled() {
        return ModList.get().isLoaded(this.getId());
    }

    @Override
    public String getId() {
        return Names.Mods.BETTER_WEATHER;
    }

    @Override
    public String getName() {
        return this.getId();
    }

    @Override
    public void registerSeasonLogic(@Nonnull IAgriSeasonLogic seasonLogic) {
        if(SeasonLogic.getInstance().getOwner() == null) {
            SeasonLogic.getInstance().claim(this, BetterWeatherSeasonGetter.getInstance());
        }
    }
}
