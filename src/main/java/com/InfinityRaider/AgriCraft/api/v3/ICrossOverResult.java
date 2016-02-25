package com.InfinityRaider.AgriCraft.api.v3;

import com.InfinityRaider.AgriCraft.api.v2.ISeedStats;
import net.minecraft.item.Item;

/**
 * This interface is an object holding data to be applied to a crop after a mutation (be it a spread or an actual mutation) has occurred.
 *
 * First a random number is generated, if it is smaller than the value returned by getChance(), this result will be applied to the cross crop.
 * The crop will have a new plant with 0% growth and the seed's item and meta will be determined by the getSeed() and getMeta() methods.
 * The crop's stats will be determined by the getStats() method.
 */
public interface ICrossOverResult {
    /**
     * @return the Item corresponding to the result of the mutation
     */
    Item getSeed();

    /**
     * @return the meta data corresponding to the result of the mutation
     */
    int getMeta();

    /**
     * The value returned from this determines if a mutation was successful.
     * In default Agricraft behaviour this depends on the tier of the resulting seed.
     *
     * @return the chance that this result will be successfully applied to the crop, should be in the interval [0, 1] (0: never successful, 1: always successful)
     */
    double getChance();

    /**
     * @return an ISeedStats object holding the stats for the new plant obtained through mutation.
     */
    ISeedStats getStats();
}
