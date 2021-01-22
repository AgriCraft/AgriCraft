package com.infinityraider.agricraft.api.v1.genetics;

import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * An interface for managing mutations.
 *
 * @author AgriCraft Team
 */
public interface IAgriMutationRegistry extends IAgriRegistry<IAgriMutation> {

    /**
     * Creates and registers a new mutation.
     *
     * @param id the id of the mutation, may not be null.
     * @param chance the chance of the mutation occurring as a normalized p-value.
     * @param childId PlantID for the child plant;
     * @param parentIds PlantIDs for the parent plants.
     * @return {@literal true} if the mutation had not been registered before, {@literal false}
     * otherwise.
     */
    boolean add(@Nonnull String id, double chance, @Nonnull String childId, @Nonnull String... parentIds);

    /**
     * Creates and registers a new mutation.
     *
     * @param id the id of the mutation, may not be null.
     * @param chance the chance of the mutation occurring as a normalized p-value.
     * @param childId PlantID for the child plant;
     * @param parentIds PlantIDs for the parent plants.
     * @return {@literal true} if the mutation had not been registered before, {@literal false}
     * otherwise.
     */
    boolean add(@Nonnull String id, double chance, @Nonnull String childId, @Nonnull List<String> parentIds);

    /**
     * Calculates the complexity for a plant, the deeper down the mutation tree, the complexer a plant is.
     * By default, plants with higher complexity are implemented to be dominant, from a genetics point of view,
     * relative to plants with lower complexities.
     *
     * @param plant the plant
     * @return integer value representing the complexity
     */
    int complexity(IAgriPlant plant);

}
