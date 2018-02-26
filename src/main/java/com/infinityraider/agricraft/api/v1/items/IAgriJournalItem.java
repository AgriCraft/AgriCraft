package com.infinityraider.agricraft.api.v1.items;

import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;

/**
 * This interface is implemented in AgriCraft's agricultural item class for the journal. Can be used
 * to extract/add information from the journal.
 *
 * e.g. ((IJournal) stack.getItem()).isSeedDiscovered(stack, someSeed);
 *
 * If, for some reason you do not wish to interact with this interface, you can also use methods
 * provided by APIv2
 */
public interface IAgriJournalItem extends IAgriCraftItem {

    /**
     * Checks if a plant is discovered in the journal.
     *
     * @param journal an ItemStack holding the journal.
     * @param plant the plant to check for in the journal.
     * @return if the seed is discovered in the journal.
     */
    boolean isSeedDiscovered(@Nonnull ItemStack journal, @Nullable IAgriPlant plant);

    /**
     * This adds an entry the journal, for example when a seed is analyzed in the seed analyzer this
     * method is called. This internally checks if the seed is discovered already before adding to
     * prevent duplicate entries
     *
     * @param journal an ItemStack holding the journal.
     * @param plant the plant to add to the journal.
     */
    void addEntry(@Nonnull ItemStack journal, @Nullable IAgriPlant plant);

    /**
     * Gets an ArrayList containing all seeds discovered in this journal
     *
     * @param journal an ItemStack holding the journal
     * @return an ArrayList containing an ItemStack for every discovered seed (the list may be empty
     * but will never be null)
     */
    @Nonnull
    List<IAgriPlant> getDiscoveredSeeds(@Nonnull ItemStack journal);
}
