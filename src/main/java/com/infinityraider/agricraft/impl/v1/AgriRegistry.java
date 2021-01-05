package com.infinityraider.agricraft.impl.v1;

import com.infinityraider.agricraft.api.v1.misc.IAgriRegisterable;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;

import javax.annotation.Nullable;
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
public class AgriRegistry<T extends IAgriRegisterable<T>> implements IAgriRegistry<T> {

    private final ConcurrentMap<String, T> registry;
    private final String name;
    private final Class<T> clazz;

    protected AgriRegistry(String name, Class<T> clazz) {
        this.registry = new ConcurrentHashMap<>();
        this.name = name;
        this.clazz = clazz;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Class<T> clazz() {
        return this.clazz;
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
    public boolean add(@Nullable T object) {
        return object != null && (this.registry.putIfAbsent(object.getId(), object) == null);
    }

    @Override
    public boolean remove(@Nullable T element) {
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
