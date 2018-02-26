/*
 */
package com.infinityraider.agricraft.impl.v1;

import com.infinityraider.agricraft.api.v1.misc.IAgriRegisterable;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

/**
 * Basic IAgriRegistry implementation.
 *
 * @param <T> The type of the registry.
 */
public class AgriRegistry<T extends IAgriRegisterable> implements IAgriRegistry<T> {

    private final ConcurrentMap<String, T> registry;

    public AgriRegistry() {
        this.registry = new ConcurrentHashMap<>();
    }

    @Override
    public boolean has(String id) {
        return (id != null) && (this.registry.containsKey(id));
    }

    @Override
    public boolean has(T element) {
        return Optional.ofNullable(element)
                .map(IAgriRegisterable::getId)
                .filter(registry::containsValue)
                .isPresent();
    }

    @Override
    public Optional<T> get(String id) {
        return Optional.ofNullable(id)
                .map(this.registry::get);
    }

    @Override
    public boolean add(T plant) {
        return (this.registry.putIfAbsent(plant.getId(), plant) == null);
    }

    @Override
    public boolean remove(T element) {
        return this.registry.remove(element.getId()) != null;
    }

    @Override
    public Collection<T> all() {
        return Collections.unmodifiableCollection(this.registry.values());
    }

    @Override
    public Set<String> ids() {
        return Collections.unmodifiableSet(this.registry.keySet());
    }

    @Override
    public Stream<T> stream() {
        return this.registry.values().stream();
    }

}
