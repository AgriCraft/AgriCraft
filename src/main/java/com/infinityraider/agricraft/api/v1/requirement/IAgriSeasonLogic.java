package com.infinityraider.agricraft.api.v1.requirement;

import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

/**
 * Interface to access AgriCraft season logic
 *
 * Use to fetch AgriCraft seasons for a given world an position
 * Use to register a season transformer for mods adding seasons
 *
 * The instance of this class is retrieved via AgriApi.getSeasonLogic()
 */
public interface IAgriSeasonLogic {
    /**
     * @return true if a season mod is present and has claimed the AgriCraft season logic
     */
    boolean isActive();

    /**
     * Fetches the season for a given position in the world
     *
     * @param world the world
     * @param pos the position
     * @return the season, will return ANY if no season logic exists
     */
    AgriSeason getSeason(World world, BlockPos pos);

    /**
     * Fetches the plugin which has currently claimed the logic.
     * It is possible to fetch its mod id from the plugin
     *
     * @return the plugin currently controlling season logic, or null if there is none
     */
    @Nullable
    IAgriPlugin getOwner();

    /**
     * Claims the season logic by the given plugin and season getter
     *
     * @param plugin the plugin responsible for the season logic
     * @param getter the getter
     */
    void claim(IAgriPlugin plugin, BiFunction<World, BlockPos, AgriSeason> getter);
}
