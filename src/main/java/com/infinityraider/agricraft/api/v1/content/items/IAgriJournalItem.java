package com.infinityraider.agricraft.api.v1.content.items;

import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.infinityraider.agricraft.api.v1.util.IAgriItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * This interface is implemented in AgriCraft's agricultural item class for the journal. Can be used
 * to extract/add information from the journal.
 *
 * e.g. ((IJournal) stack.getItem()).isSeedDiscovered(stack, someSeed);
 *
 * If, for some reason you do not wish to interact with this interface, you can also use methods
 * provided by APIv2
 */
public interface IAgriJournalItem extends IAgriItem {

    /**
     * Checks if a plant is discovered in the journal.
     *
     * @param journal an ItemStack holding the journal.
     * @param plant the plant to check for in the journal.
     * @return if the seed is discovered in the journal.
     */
    boolean isPlantDiscovered(@Nonnull ItemStack journal, @Nullable IAgriPlant plant);

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

    /**
     * Gets the index for the current page of the journal
     * @param journal the journal
     * @return the index
     */
    int getCurrentPageIndex(@Nonnull ItemStack journal);

    /**
     * Gets an unmodifiable list of all the pages in this journal
     * @param journal an ItemStack holding the journal
     * @return a list of all pages in the journal, in order
     */
    @Nonnull
    List<IPage> getPages(@Nonnull ItemStack journal);

    /**
     * Interface representing pages inside the journal
     */
    interface IPage {
        /**
         * This method gives a unique reference to a {@link com.infinityraider.agricraft.api.v1.client.IJournalDataDrawer}
         * The journal data drawer handles the drawing for this page on the client side
         * @return a unique id for a data drawer
         */
        @Nonnull
        ResourceLocation getDataDrawerId();

        /**
         * Callback for when a player opens this page
         * @param player the player
         * @param stack the stack holding the journal
         * @param journal the journal
         */
        void onPageOpened(PlayerEntity player, ItemStack stack, IAgriJournalItem journal);

    }
}
