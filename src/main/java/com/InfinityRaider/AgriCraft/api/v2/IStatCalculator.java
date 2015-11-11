package com.InfinityRaider.AgriCraft.api.v2;

import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Interface to create custom Stat Calculator logic
 * use API.setStatCalculator(IStatCalculator calculator) to set the active calculator
 */
public interface IStatCalculator {
    /**
     * Calculates the stats for a mutation or spread result
     * @param result an ItemStack containing the seed of the new plant
     * @param input a List containing all neighbouring crops
     * @param mutation if a mutation occurred, this is false if the plant simply spread to a cross crop
     * @return an ISeedStats object containing the resulting stats
     */
    ISeedStats calculateStats(ItemStack result, List<? extends ICrop> input, boolean mutation);
}
