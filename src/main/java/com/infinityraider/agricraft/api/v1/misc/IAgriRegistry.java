/*
 */
package com.infinityraider.agricraft.api.v1.misc;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The root interface for AgriCraft registries.
 *
 * @param <T> The type of the elements to be stored in the registry.
 */
public interface IAgriRegistry<T extends IAgriRegisterable> {

    /**
     * Determines if the registry contains an entry with the given id.
     *
     * @param id the id to search for in the registry.
     * @return {@literal true} if an element has been registered to the registry with the given id,
     * {@literal false} otherwise.
     */
    boolean has(@Nullable String id);

    /**
     * Determines if the registry contains a registration for this element.
     *
     * @param element the element to determine if registered.
     * @return {@literal true} if the registry has an exact registration for the given element.
     */
    boolean has(@Nullable T element);

    /**
     * Fetches the element in the registry with the given id, or else returns the empty optional.
     *
     * @param id the id of the element in the registry to be fetched.
     * @return the element with the given id in the registry, or the empty optional.
     */
    @Nonnull
    Optional<T> get(@Nullable String id);

    /**
     * Registers the given element in the registry.
     *
     * @param element the element to be registered in the registry.
     * @return {@literal true} if the element was registered, {@literal false} otherwise (i.e. in
     * the case of a conflict).
     */
    boolean add(@Nullable T element);

    /**
     * Removes the given element from the registry.
     *
     * @param element the element to be removed from the registry.
     * @return {@literal true} if the element was removed, {@literal false} otherwise (i.e. in the
     * case that the element was not actually in the registry).
     */
    boolean remove(@Nullable T element);

    /**
     * Fetches an unmodifiable view of all the elements in the registry.
     * <p>
     * Notice, this method should probably be avoided, in favor of using {@link #stream()}, given
     * that the latter method can directly connect to the backing collection without having to wrap
     * it.
     *
     * @return an unmodifiable view of all the elements in the registry.
     */
    @Nonnull
    Collection<T> all();

    /**
     * Fetches an unmodifiable view of all the ids registered in the registry.
     *
     * @return an unmodifiable view of all the ids registered in the registry.
     */
    @Nonnull
    Set<String> ids();

    /**
     * Streams all of the elements registered in the registry.
     *
     * @return a stream of all the elements registered in the registry.
     */
    @Nonnull
    Stream<T> stream();

}
