package com.infinityraider.agricraft.impl.v1.requirement;

import com.google.common.collect.Lists;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.api.v1.requirement.AgriSeason;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSeasonLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.BiFunction;

public abstract class SeasonPlugin implements IAgriPlugin {
    private static final List<String> SEASON_MODS = Lists.newArrayList();

    public static List<String> getSeasonMods() {
        return SEASON_MODS;
    }

    protected SeasonPlugin() {
        SEASON_MODS.add(this.getId());
    }

    public static String getConfigComment() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < SEASON_MODS.size(); i++) {
            if(i == 0) {
                builder.append("\"");
            } else {
                builder.append(", \"");
            }
            builder.append(SEASON_MODS.get(i));
            builder.append("\"");
        }
        return builder.toString();
    }

    @Override
    public final boolean isEnabled() {
        return ModList.get().isLoaded(this.getId());
    }

    @Override
    public final void registerSeasonLogic(@Nonnull IAgriSeasonLogic seasonLogic) {
        if(SeasonLogic.getInstance().getOwner() == null) {
            if(SEASON_MODS.stream().filter(id -> !id.equalsIgnoreCase(this.getId())).anyMatch(id -> ModList.get().isLoaded(id))) {
                if(AgriCraft.instance.getConfig().getSeasonLogicMod().equalsIgnoreCase(this.getId())) {
                    SeasonLogic.getInstance().claim(this, this.getSeasonGetter());
                }
            } else {
                SeasonLogic.getInstance().claim(this, this.getSeasonGetter());
            }
        }
    }

    protected abstract BiFunction<Level, BlockPos, AgriSeason> getSeasonGetter();
}
