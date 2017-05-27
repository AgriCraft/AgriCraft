/*
 */
package com.infinityraider.agricraft.api.mutation;

import java.util.Collection;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

/**
 * An interface for managing mutations.
 *
 * @author AgriCraft Team
 */
public interface IAgriMutationRegistry {

    /**
     * Gets a unmodifiable list of all mutations currently registered. Mutations
     * are populated onServerAboutToStartEvent, so any calls before that will
     * return the empty list.
     *
     * @return A unmodifiable view of the set of all mutations currently
     * registered.
     */
    @Nonnull
    Collection<IAgriMutation> getMutations();
    
    /**
     * Streams all the currently registered mutations. Mutations are registered
     * during the onServerAboutToStart event, so any calls to this method before
     * that will return indeterminate results.
     * <p>
     * Notice, that if you want a stream of mutations, this method is highly
     * preferred over {@link #getMutations()} given that this method can
     * directly reference the mutation list instead of having to wrap it.
     *
     * @return A stream consisting of all currently registered mutations.
     */
    @Nonnull
    Stream<IAgriMutation> streamMutations();

    /**
     * Creates and registers a new mutation.
     *
     * @param chance the chance of the mutation occurring as a normalized
     * p-value.
     * @param childId PlantID for the child plant;
     * @param parentIds PlantIDs for the parent plants.
     * @return {@literal true} if the mutation had not been registered before,
     * {@literal false} otherwise.
     */
    boolean addMutation(double chance, String childId, String... parentIds);

    /**
     * Registers the given mutation.
     *
     * @param mutation The mutation to be registered.
     * @return {@literal true} if the mutation had not been registered before,
     * {@literal false} otherwise.
     */
    boolean addMutation(IAgriMutation mutation);

}
