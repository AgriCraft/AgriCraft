package com.InfinityRaider.AgriCraft.api.v2;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;

/**
 * This interface is implemented in AgriCraft's agricultural item class for the journal.
 * Can be used to extract/add information from the journal.
 *
 * e.g.
 * ((IJournal) stack.getItem()).isSeedDiscovered(stack, someSeed);
 *
 * If, for some reason you do not wish to interact with this interface, you can also use methods provided by APIv2
 */
public interface IJournal {
    /**
     * Checks if a seed is discovered in the journal
     * @param journal an ItemStack holding the journal
     * @param seed an ItemStack containing a seed
     * @return if the seed is discovered in the journal
     */
    boolean isSeedDiscovered(ItemStack journal, ItemStack seed);

    /**
     * This adds an entry the journal, for example when a seed is analyzed in the seed analyzer this method is called.
     * This internally checks if the seed is discovered already before adding to prevent duplicate entries
     * @param journal an ItemStack holding the journal
     * @param seed an ItemStack containing a seed
     */
    void addEntry(ItemStack journal, ItemStack seed);

    /**
     * Gets an ArrayList containing all seeds discovered in this journal
     * @param journal an ItemStack holding the journal
     * @return an ArrayList containing an ItemStack for every discovered seed (the list may be empty but will never be null)
     */
    ArrayList<ItemStack> getDiscoveredSeeds(ItemStack journal);
}
