package com.infinityraider.agricraft.api.v3.mutation;

import javax.annotation.Nonnull;
import com.infinityraider.agricraft.api.v3.plant.IAgriPlant;

/**
 * An interface representing a mutation, which is a relation between parents and
 * a child.
 *
 * It is reccomended that you do not implement this interface yourself, as it is
 * provided with the intention of being used as a reference.
 *
 * @author AgriCraft
 * @since v2
 */
public interface IAgriMutation {

	/**
	 * Retrieves the probability of the mutation occurring. Recommended to be
	 * within the normalized p-value bounds of 0.0 and 1.0.
	 *
	 * @return the probability of the mutation occurring.
	 */
	double getChance();

	/**
	 * Gets the plant that results from the completion of the mutation.
	 *
	 * @return The child plant generated from the mutation.
	 */
	@Nonnull
	IAgriPlant getChild();

	/**
	 * Gets a list of the required neighboring plants for the mutation to occur.
	 * These required plants are also known as <i>parents</i>, which are bred in
	 * the mutation to produce the <i>child</i>.
	 *
	 * @return a list of the parent plants for the mutation.
	 */
	@Nonnull
	IAgriPlant[] getParents();

}
