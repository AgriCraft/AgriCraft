package com.InfinityRaider.AgriCraft.api.v3;

import com.InfinityRaider.AgriCraft.api.v2.*;
import com.InfinityRaider.AgriCraft.api.v2.ICrop;
import net.minecraft.item.Item;

import java.util.List;

public interface IStatCalculator extends com.InfinityRaider.AgriCraft.api.v2.IStatCalculator {
    /**
     * Calculates the stats for a mutation or spread result
     * @param result an ItemStack containing the seed of the new plant
     * @param input a List containing all neighbouring crops
     * @param mutation if a mutation occurred, this is false if the plant simply spread to a cross crop
     * @return an ISeedStats object containing the resulting stats
     */
    ISeedStats calculateStats(Item result, int resultMeta, List<? extends ICrop> input, boolean mutation);
}
