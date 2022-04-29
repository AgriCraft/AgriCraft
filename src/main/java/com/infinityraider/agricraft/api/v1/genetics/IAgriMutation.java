package com.infinityraider.agricraft.api.v1.genetics;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.util.IAgriRegisterable;
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
public interface IAgriMutation extends IAgriRegisterable<IAgriMutation> {

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

    /**
     * @return a list of all conditions needed/enabling this mutation
     */
    @Nonnull
    List<Condition> getConditions();

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

    /**
     * An interface representing conditions for mutations
     */
    interface Condition {
        /**
         * Checks the result of the condition
         * @param crop the crop on which the mutation would happen
         * @param mutation the mutation which has been selected
         * @return the result for this condition at the given crop
         */
        ConditionResult getResult(IAgriCrop crop, IAgriMutation mutation);
    }

    /**
     * enum representing the possible results of a trigger
     */
    enum ConditionResult {
        /** the condition forbids the mutation, this overrules FORCE */
        FORBID,
        /** the condition forces the mutation to happen */
        FORCE,
        /** the condition is ignored and default mutation logic is followed */
        PASS
    }

}
