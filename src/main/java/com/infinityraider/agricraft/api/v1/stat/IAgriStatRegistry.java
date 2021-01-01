package com.infinityraider.agricraft.api.v1.stat;

import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;

public interface IAgriStatRegistry extends IAgriRegistry<IAgriStat> {

    /**
     * @return the native AgriCraft gain stat, which controls the amount of fruits obtained when harvesting a crop
     */
    IAgriStat gainStat();

    /**
     * @return the native AgriCraft growth stat, which controls the growth rate of a crop
     */
    IAgriStat growthStat();

    /**
     * @return the native AgriCraft strength stat, which controls the probability of a crop to overcome growth conditions
     */
    IAgriStat strengthStat();

    /**
     * @return the native AgriCraft fertility stat, which controls the probability of a crop to produce new crops
     */
    IAgriStat fertilityStat();

    /**
     * @return the native AgriCraft resistance stat, which controls the resistance of a crop against weeds
     */
    IAgriStat resistanceStat();
}
