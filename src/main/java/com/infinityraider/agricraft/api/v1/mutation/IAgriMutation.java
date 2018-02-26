package com.infinityraider.agricraft.api.v1.mutation;

import com.infinityraider.agricraft.api.v1.misc.IAgriRegisterable;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An interface representing a mutation, which is a relation between parents and a child.
 *
 * It is recommended that you do not implement this interface yourself, as it is provided with the
 * intention of being used as a reference.
 *
 * @author AgriCraft
 * @since v2
 */
public interface IAgriMutation extends IAgriRegisterable {

    /**
     * Retrieves the probability of the mutation occurring. Recommended to be within the normalized
     * p-value bounds of 0.0 and 1.0.
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
     * Gets a list of the required neighboring plants for the mutation to occur. These required
     * plants are also known as <i>parents</i>, which are bred in the mutation to produce the
     * <i>child</i>.
     *
     * @return a list of the parent plants for the mutation.
     */
    @Nonnull
    List<IAgriPlant> getParents();

    default boolean hasChild(@Nullable IAgriPlant plant) {
        return this.getChild().equals(plant);
    }

    default boolean hasParent(@Nullable IAgriPlant plant) {
        return this.getParents().contains(plant);
    }

    default boolean hasParent(@Nonnull IAgriPlant... plants) {
        return this.getParents().containsAll(Arrays.asList(plants));
    }

    default boolean hasParent(@Nullable Collection<IAgriPlant> plants) {
        return this.getParents().containsAll(plants);
    }

    default boolean areParentsIn(@Nonnull IAgriPlant... plants) {
        return Arrays.asList(plants).containsAll(this.getParents());
    }

    default boolean areParentsIn(@Nullable Collection<IAgriPlant> plants) {
        return (plants != null) && (plants.containsAll(this.getParents()));
    }

}
