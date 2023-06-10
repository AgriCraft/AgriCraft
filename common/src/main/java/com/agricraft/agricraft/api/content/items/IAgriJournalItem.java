package com.agricraft.agricraft.api.content.items;

import com.agricraft.agricraft.api.plant.IAgriPlant;
import com.agricraft.agricraft.api.util.IAgriItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
	 * @param plant   the plant to check for in the journal.
	 * @return if the seed is discovered in the journal.
	 */
	boolean isPlantDiscovered(@NotNull ItemStack journal, @Nullable IAgriPlant plant);

	/**
	 * This adds an entry the journal, for example when a seed is analyzed in the seed analyzer this
	 * method is called. This internally checks if the seed is discovered already before adding to
	 * prevent duplicate entries
	 *
	 * @param journal an ItemStack holding the journal.
	 * @param plant   the plant to add to the journal.
	 */
	void addEntry(@NotNull ItemStack journal, @Nullable IAgriPlant plant);

	/**
	 * Gets an ArrayList containing all seeds discovered in this journal
	 *
	 * @param journal an ItemStack holding the journal
	 * @return an ArrayList containing an ItemStack for every discovered seed (the list may be empty
	 * but will never be null)
	 */
	@NotNull
	List<IAgriPlant> getDiscoveredSeeds(@NotNull ItemStack journal);

	/**
	 * Gets the index for the current page of the journal
	 *
	 * @param journal the journal
	 * @return the index
	 */
	int getCurrentPageIndex(@NotNull ItemStack journal);

	/**
	 * Sets the index for the current page of the journal
	 *
	 * @param index   the index
	 * @param journal the journal
	 */
	void setCurrentPageIndex(@NotNull ItemStack journal, int index);

	/**
	 * Gets an unmodifiable list of all the pages in this journal
	 *
	 * @param journal an ItemStack holding the journal
	 * @return a list of all pages in the journal, in order
	 */
	@NotNull
	List<IPage> getPages(@NotNull ItemStack journal);

	/**
	 * Interface representing pages inside the journal
	 */
	interface IPage {

		// FIXME: update
		/**
		 * This method gives a unique reference to a {@link com.agricraft.agricraft.api.client.IJournalDataDrawer}
		 * The journal data drawer handles the drawing for this page on the client side
		 *
		 * @return a unique id for a data drawer
		 */
		@NotNull
		ResourceLocation getDataDrawerId();

		/**
		 * @return The type of this page, helps to identify different pages
		 */
		@NotNull
		Type getPageType();

		/**
		 * Callback for when a player opens this page
		 * Called on the server thread
		 *
		 * @param player  the player
		 * @param stack   the stack holding the journal
		 * @param journal the journal
		 */
		void onPageOpened(Player player, ItemStack stack, IAgriJournalItem journal);

		/**
		 * Enum describing types of pages, helps to identify different pages
		 */
		enum Type {
			/**
			 * For the front page of the journal
			 */
			FRONT,

			/**
			 * For the introduction page of the journal
			 */
			INTRO,

			/**
			 * For pages containing documentation
			 */
			DOCS,

			/**
			 * For pages containing information about a plant
			 */
			PLANT,

			/**
			 * for pages containing mutations (usually after a plant page for mutations that did not fit on it)
			 */
			MUTATIONS,

			/**
			 * For any other purpose (does not occur in vanilla AgriCraft)
			 */
			OTHER
		}

	}

}
