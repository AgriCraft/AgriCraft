package com.InfinityRaider.AgriCraft.farming.mutation;

import com.InfinityRaider.AgriCraft.api.v2.ISeedStats;
import com.InfinityRaider.AgriCraft.api.v3.ICrossOverResult;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import net.minecraft.item.Item;

/**
 * Represents the result of a specific <code>INewSeedStrategy</code> containing
 * the seed plus meta and the chance to happen.
 */
public class CrossOverResult implements ICrossOverResult {
    private Item seed;
    private int meta;
    private double chance;
    private ISeedStats stats;

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
    public void setSeedAndMeta(Item seed, int meta) {
        if(CropPlantHandler.isValidSeed(seed, meta)) {
            this.seed = seed;
            this.meta = meta;
        }
    }

    @Override
    public double getChance() {
        return chance;
    }

    @Override
    public void setChance(double chance) {
        this.chance = chance;
    }

    public ISeedStats getStats() {
        return stats;
    }

    @Override
    public void setSeedStats(ISeedStats stats) {
        this.stats = stats;
    }
}
