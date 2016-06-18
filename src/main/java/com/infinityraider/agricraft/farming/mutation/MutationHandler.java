package com.infinityraider.agricraft.farming.mutation;

import com.infinityraider.agricraft.compat.jei.AgriCraftJEIPlugin;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import com.infinityraider.agricraft.api.v3.core.IAgriPlant;
import com.infinityraider.agricraft.api.v3.core.IAgriCrop;
import com.infinityraider.agricraft.api.v3.core.IAgriMutation;

public abstract class MutationHandler {

	private static final @Nonnull
	List<IAgriMutation> mutations = new ArrayList<>();

	//gets all the possible crossovers
	public static IAgriMutation[] getCrossOvers(List<IAgriCrop> crops) {
		IAgriCrop[] parents = MutationHandler.filterParents(crops);
		ArrayList<IAgriMutation> list = new ArrayList<>();
		for(int i = 0; i < parents.length; i++) {
			for (int j = i + 1; j < parents.length; j++) {
				list.addAll(getMutationsFromParent(parents[i].getPlant(), parents[j].getPlant()));
			}
		}
		return cleanMutationArray(list.toArray(new IAgriMutation[list.size()]));
	}

	//gets an array of all the possible parents from the array containing all the neighbouring crops
	private static IAgriCrop[] filterParents(List<IAgriCrop> input) {
		ArrayList<IAgriCrop> list = new ArrayList<>();
		for (IAgriCrop crop : input) {
			if (crop != null && crop.isMature()) {
				list.add(crop);
			}
		}
		return list.toArray(new IAgriCrop[list.size()]);
	}

	//finds the product of two parents
	private static ArrayList<IAgriMutation> getMutationsFromParent(IAgriPlant parent1, IAgriPlant parent2) {
		ArrayList<IAgriMutation> list = new ArrayList<>();
		for (IAgriMutation mutation : mutations) {
			boolean p1 = false;
			boolean p2 = false;
			for (IAgriPlant p : mutation.getParents()) {
				if (!p1 && p.equals(parent1)) {
					p1 = true;
				} else if (!p2 && p.equals(parent2)) {
					p2 = true;
				}
				if (p1 && p2) {
					list.add(mutation);
					break;
				}
			}
		}
		return list;
	}

	//removes null instance from a mutations array
	private static IAgriMutation[] cleanMutationArray(IAgriMutation[] input) {
		ArrayList<IAgriMutation> list = new ArrayList<>();
		for (IAgriMutation mutation : input) {
			if (mutation.getChild() != null) {
				list.add(mutation);
			}
		}
		return list.toArray(new IAgriMutation[list.size()]);
	}

	//public methods to read data from the mutations and parents arrays
	//gets all the mutations
	public static IAgriMutation[] getMutations() {
		return mutations.toArray(new IAgriMutation[mutations.size()]);
	}

	//gets all the mutations this crop can mutate to
	public static IAgriMutation[] getMutationsFromParent(IAgriPlant parent) {
		ArrayList<IAgriMutation> list = new ArrayList<>();
		for (IAgriMutation mutation : mutations) {
			for (IAgriPlant p : mutation.getParents()) {
				if (parent.equals(p)) {
					list.add(mutation);
					break;
				}
			}
		}
		return list.toArray(new IAgriMutation[list.size()]);
	}

	//gets the parents this crop mutates from
	public static IAgriMutation[] getMutationsFromChild(IAgriPlant child) {
		ArrayList<IAgriMutation> list = new ArrayList<>();
		for (IAgriMutation m : mutations) {
			if (m.getChild().equals(child)) {
				list.add(m);
			}
		}
		return list.toArray(new IAgriMutation[list.size()]);
	}

	/**
	 * Removes all mutations where the given parameter is the result of a
	 * mutation
	 *
	 * @return Removed mutations
	 */
	public static List<IAgriMutation> removeMutationsByResult(ItemStack result) {
		List<IAgriMutation> removedMutations = new ArrayList<>();
		for (Iterator<IAgriMutation> iter = mutations.iterator(); iter.hasNext();) {
			IAgriMutation mutation = iter.next();
			if (mutation.getChild().getSeed().isItemEqual(result)) {
				iter.remove();
				removedMutations.add(mutation);
			}
		}
		return removedMutations;
	}

	/**
	 * Adds the given mutation to the mutations list
	 */
	public static synchronized boolean add(IAgriMutation mutation) {
		boolean result = false;
		if (mutation != null && !mutations.contains(mutation)) {
			result = mutations.add(mutation);
			AgriCraftJEIPlugin.registerRecipe(mutation);
		}
		return result;
	}

	/**
	 * Removes the given mutation from the mutations list
	 */
	public static void remove(IAgriMutation mutation) {
		mutations.remove(mutation);
	}

	/**
	 * Adds all mutations back into the mutation list. Does not perform and
	 * validation.
	 */
	public static void addAll(Collection<? extends IAgriMutation> mutationsToAdd) {
		mutationsToAdd.forEach(MutationHandler::add);
	}
}
