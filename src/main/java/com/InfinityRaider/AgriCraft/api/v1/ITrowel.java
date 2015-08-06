package com.InfinityRaider.AgriCraft.api.v1;

import net.minecraft.item.ItemStack;

/** Interface to be implemented in items that are considered trowels */
public interface ITrowel {
    /** Return true if this trowel is currently carrying a plant */
    boolean hasSeed(ItemStack trowel);

    /** Return true if the seed currently being carried is analysed, return false if it is not or if there is no seed */
    boolean isSeedAnalysed(ItemStack trowel);

    /** This is called to analyse the seed currently being carried */
    void analyze(ItemStack trowel);

    /** This returns the seed currently on the trowel */
    ItemStack getSeed(ItemStack trowel);

    /** This returns the growthStage of the plant currently being carried */
    int getGrowthStage(ItemStack trowel);

    /** Sets the seed to the trowel, returns true if successful */
    boolean setSeed(ItemStack trowel, ItemStack seed, int growthStage);

    /** Clears the seed from the trowel */
    void clearSeed(ItemStack trowel);

    /** Gets the stats from the seed */
    ISeedStats getStats(ItemStack trowel) ;
}
