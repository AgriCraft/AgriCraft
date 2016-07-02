/*
 */
package com.infinityraider.agricraft.api.v1.mutation;

import java.util.List;
import javax.annotation.Nonnull;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutation;

/**
 * An interface for managing mutations.
 * 
 * @author AgriCraft Team
 */
public interface IAgriMutationRegistry {
	
	/**
	 * Gets a list of all mutations currently registered Mutations are populated
	 * onServerAboutToStartEvent, so any calls before that will return null
	 */
	List<IAgriMutation> getRegisteredMutations();

	/**
	 * Gets a list of all mutations that have this stack as a parent Mutations
	 * are populated onServerAboutToStartEvent, so any calls before that will
	 * return null
	 */
	List<IAgriMutation> getMutationsForParent(IAgriPlant parent);

	/**
	 * Gets a list of all mutations that have this stack as a child Mutations
	 * are populated onServerAboutToStartEvent, so any calls before that will
	 * return null
	 */
	List<IAgriMutation> getMutationsForChild(IAgriPlant child);

	/**
	 * Registers a new mutation: result = parent1 + parent2
	 *
	 * @param result ItemStack containing the resulting seed of the mutation
	 * @param parent1 ItemStack containing one parent for the mutation
	 * @param parent2 ItemStack containing the other parent for the mutation
	 * @return True if successful
	 */
	boolean addMutation(double chance, @Nonnull IAgriPlant child, @Nonnull IAgriPlant... parents);

	/**
	 * Removes all mutations that give this stack as a result
	 *
	 * @param result ItemStack containing the resulting seed for all the
	 * mutations to be removed
	 * @return True if successful
	 */
	boolean removeMutation(IAgriPlant result);
	
}
