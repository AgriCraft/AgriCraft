/*
 */
package com.infinityraider.agricraft.api.v3.registry;

import com.infinityraider.agricraft.api.v3.IAgriCraftPlant;
import com.infinityraider.agricraft.api.v3.IGrowthRequirement;
import com.infinityraider.agricraft.api.v3.IMutation;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * An interface for managing mutations.
 * 
 * @author AgriCraft Team
 */
public interface IMutationRegistry {
	
	/**
	 * Gets a list of all mutations currently registered Mutations are populated
	 * onServerAboutToStartEvent, so any calls before that will return null
	 */
	List<IMutation> getRegisteredMutations();

	/**
	 * Gets a list of all mutations that have this stack as a parent Mutations
	 * are populated onServerAboutToStartEvent, so any calls before that will
	 * return null
	 */
	List<IMutation> getMutationsForParent(IAgriCraftPlant parent);

	/**
	 * Gets a list of all mutations that have this stack as a child Mutations
	 * are populated onServerAboutToStartEvent, so any calls before that will
	 * return null
	 */
	List<IMutation> getMutationsForChild(IAgriCraftPlant child);

	/**
	 * Registers a new mutation: result = parent1 + parent2
	 *
	 * @param result ItemStack containing the resulting seed of the mutation
	 * @param parent1 ItemStack containing one parent for the mutation
	 * @param parent2 ItemStack containing the other parent for the mutation
	 * @return True if successful
	 */
	boolean addMutation(double chance, IGrowthRequirement requirement, @Nonnull IAgriCraftPlant child, @Nonnull IAgriCraftPlant... parents);

	/**
	 * Removes all mutations that give this stack as a result
	 *
	 * @param result ItemStack containing the resulting seed for all the
	 * mutations to be removed
	 * @return True if successful
	 */
	boolean removeMutation(IAgriCraftPlant result);
	
}
