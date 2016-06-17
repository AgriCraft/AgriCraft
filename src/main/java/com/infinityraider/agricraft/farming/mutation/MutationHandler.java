package com.infinityraider.agricraft.farming.mutation;

import com.infinityraider.agricraft.api.v3.IAgriCraftPlant;
import com.infinityraider.agricraft.api.v3.ICrop;
import com.infinityraider.agricraft.api.v3.IMutation;
import com.infinityraider.agricraft.compat.jei.AgriCraftJEIPlugin;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;

public abstract class MutationHandler {

	private static final @Nonnull
	List<IMutation> mutations = new ArrayList<>();

	//gets all the possible crossovers
	public static IMutation[] getCrossOvers(List<ICrop> crops) {
		ICrop[] parents = MutationHandler.filterParents(crops);
		ArrayList<IMutation> list = new ArrayList<>();
		for(int i = 0; i < parents.length; i++) {
			for (int j = i + 1; j < parents.length; j++) {
				list.addAll(getMutationsFromParent(parents[i].getPlant(), parents[j].getPlant()));
			}
		}
		return cleanMutationArray(list.toArray(new IMutation[list.size()]));
	}

	//gets an array of all the possible parents from the array containing all the neighbouring crops
	private static ICrop[] filterParents(List<ICrop> input) {
		ArrayList<ICrop> list = new ArrayList<>();
		for (ICrop crop : input) {
			if (crop != null && crop.isMature()) {
				list.add(crop);
			}
		}
		return list.toArray(new ICrop[list.size()]);
	}

	//finds the product of two parents
	private static ArrayList<IMutation> getMutationsFromParent(IAgriCraftPlant parent1, IAgriCraftPlant parent2) {
		ArrayList<IMutation> list = new ArrayList<>();
		for (IMutation mutation : mutations) {
			boolean p1 = false;
			boolean p2 = false;
			for (IAgriCraftPlant p : mutation.getParents()) {
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
	private static IMutation[] cleanMutationArray(IMutation[] input) {
		ArrayList<IMutation> list = new ArrayList<>();
		for (IMutation mutation : input) {
			if (mutation.getChild() != null) {
				list.add(mutation);
			}
		}
		return list.toArray(new IMutation[list.size()]);
	}

	//public methods to read data from the mutations and parents arrays
	//gets all the mutations
	public static IMutation[] getMutations() {
		return mutations.toArray(new IMutation[mutations.size()]);
	}

	//gets all the mutations this crop can mutate to
	public static IMutation[] getMutationsFromParent(IAgriCraftPlant parent) {
		ArrayList<IMutation> list = new ArrayList<>();
		for (IMutation mutation : mutations) {
			for (IAgriCraftPlant p : mutation.getParents()) {
				if (parent.equals(p)) {
					list.add(mutation);
					break;
				}
			}
		}
		return list.toArray(new IMutation[list.size()]);
	}

	//gets the parents this crop mutates from
	public static IMutation[] getMutationsFromChild(IAgriCraftPlant child) {
		ArrayList<IMutation> list = new ArrayList<>();
		for (IMutation m : mutations) {
			if (m.getChild().equals(child)) {
				list.add(m);
			}
		}
		return list.toArray(new IMutation[list.size()]);
	}

	/**
	 * Removes all mutations where the given parameter is the result of a
	 * mutation
	 *
	 * @return Removed mutations
	 */
	public static List<IMutation> removeMutationsByResult(ItemStack result) {
		List<IMutation> removedMutations = new ArrayList<>();
		for (Iterator<IMutation> iter = mutations.iterator(); iter.hasNext();) {
			IMutation mutation = iter.next();
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
	public static synchronized boolean add(IMutation mutation) {
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
	public static void remove(IMutation mutation) {
		mutations.remove(mutation);
	}

	/**
	 * Adds all mutations back into the mutation list. Does not perform and
	 * validation.
	 */
	public static void addAll(Collection<? extends IMutation> mutationsToAdd) {
		mutationsToAdd.forEach(MutationHandler::add);
	}
}
