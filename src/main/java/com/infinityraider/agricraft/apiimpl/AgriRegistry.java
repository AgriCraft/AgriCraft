/*
 */
package com.infinityraider.agricraft.apiimpl;

import com.infinityraider.agricraft.api.misc.IAgriRegisterable;
import com.infinityraider.agricraft.api.misc.IAgriRegistry;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

/**
 * Basic IAgriRegistry implementation.
 */
public class AgriRegistry<T extends IAgriRegisterable> implements IAgriRegistry<T> {
    
    private final ConcurrentMap<String, T> elements;

    public AgriRegistry() {
        this.elements = new ConcurrentHashMap<>();
    }

    @Override
    public boolean has(String id) {
        return this.elements.containsKey(id);
    }

    @Override
    public boolean has(T plant) {
        return this.elements.containsKey(plant.getId());
    }

    @Override
    public T get(String id) {
        return this.elements.get(id);
    }

    @Override
    public boolean add(T plant) {
        return this.elements.putIfAbsent(plant.getId(), plant) == null;
    }

    @Override
    public boolean remove(T element) {
        return this.elements.remove(element.getId()) != null;
    }

    @Override
    public Collection<T> all() {
        return Collections.unmodifiableCollection(this.elements.values());
    }

    @Override
    public Set<String> ids() {
        return Collections.unmodifiableSet(this.elements.keySet());
    }

    @Override
    public Stream<T> stream() {
        return this.elements.values().stream();
    }
    
}
