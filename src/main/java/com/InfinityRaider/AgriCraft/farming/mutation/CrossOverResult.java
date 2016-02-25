package com.InfinityRaider.AgriCraft.farming.mutation;

import com.InfinityRaider.AgriCraft.api.v2.ISeedStats;
import com.InfinityRaider.AgriCraft.api.v3.ICrossOverResult;
import net.minecraft.item.Item;

/**
 * Represents the result of a specific <code>INewSeedStrategy</code> containing
 * the seed plus meta and the chance to happen.
 */
public class CrossOverResult implements ICrossOverResult {
    private final Item seed;
    private final int meta;
    private final double chance;
    private final ISeedStats stats;

    public CrossOverResult(Item seed, int meta, double chance, ISeedStats stats) {
        this.seed = seed;
        this.meta = meta;
        this.chance = chance;
        this.stats = stats;
    }

    @Override
    public Item getSeed() {
        return seed;
    }

    @Override
    public int getMeta() {
        return meta;
    }

    @Override
    public double getChance() {
        return chance;
    }

    public ISeedStats getStats() {
        return stats;
    }
}
