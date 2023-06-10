package com.agricraft.agricraft.api.content.items;

import com.agricraft.agricraft.api.genetics.IAgriGenome;
import com.agricraft.agricraft.api.plant.IAgriPlant;
import com.agricraft.agricraft.api.stat.IAgriStat;
import com.agricraft.agricraft.api.util.IAgriItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;

/**
 * Interface representing the AgriCraft Seed Bag Item
 */
public interface IAgriSeedBagItem extends IAgriItem {

	/**
	 * Gets the seed bag contents
	 *
	 * @param stack the ItemStack containing the seed bag
	 * @return the contents
	 */
	Contents getContents(ItemStack stack);

	/**
	 * Checks if the seed bag is activated, meaning if it has the necessary enchantment
	 *
	 * @param stack the ItemStack containing the seed bag
	 * @return true if the seed bag is activated
	 */
	boolean isActivated(ItemStack stack);

	/**
	 * Increments the sorting mode index, effectively changing the sorting mode
	 *
	 * @param stack the ItemStack containing the seed bag
	 * @param delta the amount to increment with
	 * @return true if successful
	 */
	boolean incrementSorter(ItemStack stack, int delta);

	/**
	 * Gets the sorter for this seed bag at the given index
	 *
	 * @param index the index
	 * @return the sorter
	 */
	Sorter getSorter(int index);

	/**
	 * Adds a new sorter
	 *
	 * @param sorter the sorter
	 * @return the index at which the sorter is added
	 */
	int addSorter(Sorter sorter);

	/**
	 * Adds a new sorter for a stat
	 *
	 * @param stat the stat
	 * @return the index at which the sorter is added
	 */
	int addSorter(IAgriStat stat);

	/**
	 * Interface representing the contents of a seed bag
	 */
	// FIXME: update
	interface Contents /*extends IItemHandler*/ {

		/**
		 * @return the plant species currently held by the bag
		 */
		IAgriPlant getPlant();

		/**
		 * @return the number of seeds currently held by teh bag
		 */
		int getCount();

		/**
		 * @return the capacity of the bag
		 */
		int getCapacity();

		/**
		 * @return true if the bag is full
		 */
		boolean isFull();

		/**
		 * @return the sorting logic currently in use
		 */
		Sorter getSorter();

		/**
		 * @return the current sorter index
		 */
		int getSorterIndex();

		/**
		 * Sets the index to determine which sorter to use
		 *
		 * @param index the index
		 */
		void setSorterIndex(int index);

// FIXME: update
//		/**
//		 * Tries to insert a seed in the bag
//		 *
//		 * @param stack    the seed
//		 * @param simulate if the seed should be added, or if it is only a simulation
//		 * @return any remains which were not put in the bag
//		 */
//		default ItemStack insertSeed(ItemStack stack, boolean simulate) {
//			return this.insertItem(0, stack, simulate);
//		}

		/**
		 * Extracts the first seed
		 *
		 * @param amount   the amount of seeds
		 * @param simulate if the seed should be extracted, or if it is only a simulation
		 * @return the resulting ItemStack
		 */
		ItemStack extractFirstSeed(int amount, boolean simulate);


		/**
		 * Extracts the last seed
		 *
		 * @param amount   the amount of seeds
		 * @param simulate if the seed should be extracted, or if it is only a simulation
		 * @return the resulting ItemStack
		 */
		ItemStack extractLastSeed(int amount, boolean simulate);

	}

	/**
	 * Interface representing seed sorting logic
	 */
	interface Sorter extends Comparator<IAgriGenome> {

		/**
		 * @return the name of the seed sorter
		 */
		Component getName();

		/**
		 * @return a description of the seed sorter (used for item tooltips)
		 */
		Component describe();

	}

}
