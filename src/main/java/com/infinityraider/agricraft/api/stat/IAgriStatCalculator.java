package com.infinityraider.agricraft.api.stat;

import com.infinityraider.agricraft.api.crop.IAgriCrop;
import com.infinityraider.agricraft.api.mutation.IAgriMutation;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import java.util.List;

/**
 * Interface to create custom Stat Calculator logic use
 * API.setStatCalculator(IStatCalculator calculator) to set the active
 * calculator
 */
public interface IAgriStatCalculator {
    
    default IAgriStat calculateStats(IAgriPlant plant, List<? extends IAgriCrop> matureNeighbors) {
        return calculateStats(plant, matureNeighbors, false);
    }

    default IAgriStat calculateStats(IAgriMutation mutation, List<? extends IAgriCrop> matureNeighbors) {
        return calculateStats(mutation.getChild(), matureNeighbors, true);
    }

    /**
     * Calculates the stats for a mutation or spread result
     *
     * @param result an ItemStack containing the seed of the new plant
     * @param input a List containing all neighbouring crops
     * @param mutation if a mutation occurred, this is false if the plant simply
     * spread to a cross crop
     * @return an ISeedStats object containing the resulting stats
     */
    IAgriStat calculateStats(IAgriPlant child, List<? extends IAgriCrop> input, boolean mutation);

}
