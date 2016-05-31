package com.infinityraider.agricraft.farming.mutation;

import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.tileentity.TileEntityCrop;
import com.infinityraider.agricraft.compatibility.jei.AgriCraftJEIPlugin;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;

public abstract class MutationHandler {

	private static final @Nonnull List<Mutation> mutations = new ArrayList<>();

	//gets all the possible crossovers
	public static Mutation[] getCrossOvers(List<TileEntityCrop> crops) {
		TileEntityCrop[] parents = MutationHandler.filterParents(crops);
		ArrayList<Mutation> list = new ArrayList<>();
		switch (parents.length) {
			case 2:
				list.addAll(MutationHandler.getMutationsFromParent(parents[0], parents[1]));
				break;
			case 3:
				list.addAll(MutationHandler.getMutationsFromParent(parents[0], parents[1]));
				list.addAll(MutationHandler.getMutationsFromParent(parents[0], parents[2]));
				list.addAll(MutationHandler.getMutationsFromParent(parents[1], parents[2]));
				break;
			case 4:
				list.addAll(MutationHandler.getMutationsFromParent(parents[0], parents[1]));
				list.addAll(MutationHandler.getMutationsFromParent(parents[0], parents[2]));
				list.addAll(MutationHandler.getMutationsFromParent(parents[0], parents[3]));
				list.addAll(MutationHandler.getMutationsFromParent(parents[1], parents[2]));
				list.addAll(MutationHandler.getMutationsFromParent(parents[1], parents[3]));
				list.addAll(MutationHandler.getMutationsFromParent(parents[2], parents[3]));
				break;
		}
		return cleanMutationArray(list.toArray(new Mutation[list.size()]));
	}

	//gets an array of all the possible parents from the array containing all the neighbouring crops
	private static TileEntityCrop[] filterParents(List<TileEntityCrop> input) {
		ArrayList<TileEntityCrop> list = new ArrayList<>();
		for (TileEntityCrop crop : input) {
			if (crop != null && crop.isMature()) {
				list.add(crop);
			}
		}
		return list.toArray(new TileEntityCrop[list.size()]);
	}

	//finds the product of two parents
	private static ArrayList<Mutation> getMutationsFromParent(TileEntityCrop parent1, TileEntityCrop parent2) {
		Item seed1 = parent1.getSeedStack().getItem();
		Item seed2 = parent2.getSeedStack().getItem();
		int meta1 = parent1.getSeedStack().getItemDamage();
		int meta2 = parent2.getSeedStack().getItemDamage();
		ArrayList<Mutation> list = new ArrayList<>();
		for (Mutation mutation : mutations) {
			ItemStack parent1Stack = mutation.getParents()[0];
			ItemStack parent2Stack = mutation.getParents()[1];
			if ((seed1 == parent1Stack.getItem() && seed2 == parent2Stack.getItem()) && (meta1 == parent1Stack.getItemDamage() && meta2 == parent2Stack.getItemDamage())) {
				list.add(mutation);
			}
			if ((seed1 == parent2Stack.getItem() && seed2 == parent1Stack.getItem()) && (meta1 == parent2Stack.getItemDamage() && meta2 == parent1Stack.getItemDamage())) {
				list.add(mutation);
			}
		}
		return list;
	}

	//removes null instance from a mutations array
	private static Mutation[] cleanMutationArray(Mutation[] input) {
		ArrayList<Mutation> list = new ArrayList<>();
		for (Mutation mutation : input) {
			if (mutation.getResult() != null) {
				list.add(mutation);
			}
		}
		return list.toArray(new Mutation[list.size()]);
	}

	//public methods to read data from the mutations and parents arrays
	//gets all the mutations
	public static Mutation[] getMutations() {
		return mutations.toArray(new Mutation[mutations.size()]);
	}

	//gets all the mutations this crop can mutate to
	public static Mutation[] getMutationsFromParent(ItemStack stack) {
		ArrayList<Mutation> list = new ArrayList<>();
		for (Mutation mutation : mutations) {
			ItemStack parent1Stack = mutation.getParents()[0];
			ItemStack parent2Stack = mutation.getParents()[1];
			if (parent1Stack.getItem() == stack.getItem() && parent1Stack.getItemDamage() == stack.getItemDamage()) {
				list.add(new Mutation(mutation));
			}
			if (parent2Stack.getItem() == stack.getItem() && parent2Stack.getItemDamage() == stack.getItemDamage()) {
				if (parent2Stack.getItem() == parent1Stack.getItem() && parent2Stack.getItemDamage() == parent1Stack.getItemDamage()) {
					continue;
				}
				list.add(new Mutation(mutation));
			}
		}

		return list.toArray(new Mutation[list.size()]);
	}

	public static Mutation[] getMutationsFromChild(Item seed, int meta) {
		return getMutationsFromChild(new ItemStack(seed, 1, meta));
	}

	//gets the parents this crop mutates from
	public static Mutation[] getMutationsFromChild(ItemStack stack) {
		ArrayList<Mutation> list = new ArrayList<>();
		if (CropPlantHandler.isValidSeed(stack)) {
			for (Mutation mutation : mutations) {
				if (mutation.getResult().getItem() == stack.getItem() && mutation.getResult().getItemDamage() == stack.getItemDamage()) {
					list.add(new Mutation(mutation));
				}
			}
		}
		return list.toArray(new Mutation[list.size()]);
	}

	/**
	 * Removes all mutations where the given parameter is the result of a
	 * mutation
	 *
	 * @return Removed mutations
	 */
	public static List<Mutation> removeMutationsByResult(ItemStack result) {
		List<Mutation> removedMutations = new ArrayList<>();
		for (Iterator<Mutation> iter = mutations.iterator(); iter.hasNext();) {
			Mutation mutation = iter.next();
			if (mutation.getResult().isItemEqual(result)) {
				iter.remove();
				removedMutations.add(mutation);
			}
		}
		return removedMutations;
	}

	/**
	 * Adds the given mutation to the mutations list
	 */
	public static synchronized void add(Mutation mutation) {
		if (mutation != null && !mutations.contains(mutation)) {
			mutations.add(mutation);
			AgriCraftJEIPlugin.registerRecipe(mutation);
		}
	}

	/**
	 * Removes the given mutation from the mutations list
	 */
	public static void remove(Mutation mutation) {
		mutations.remove(mutation);
	}

	/**
	 * Adds all mutations back into the mutation list. Does not perform and
	 * validation.
	 */
	public static void addAll(Collection<? extends Mutation> mutationsToAdd) {
		mutationsToAdd.forEach(MutationHandler::add);
	}
}
