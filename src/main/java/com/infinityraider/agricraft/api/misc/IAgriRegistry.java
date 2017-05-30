/*
 */
package com.infinityraider.agricraft.api.misc;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

/**
 * The root interface for AgriCraft registries.
 * 
 * @param <T> The type of the elements to be stored in the registry.
 */
public interface IAgriRegistry<T extends IAgriRegisterable> {

    boolean has(String id);

    boolean has(T plant);

    T get(String id);

    boolean add(T plant);

    boolean remove(T plant);

    Collection<T> all();
    
    Set<String> ids();

    Stream<T> stream();

}
