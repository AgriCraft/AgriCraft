package com.agricraft.agricraft.api;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

public abstract class AgriRegistry<T extends AgriRegistrable> implements Iterable<T> {

	private final ConcurrentMap<String, T> registry;

	protected AgriRegistry() {
		this.registry = new ConcurrentHashMap<>();
	}

	public boolean has(String id) {
		return (id != null) && (this.registry.containsKey(id));
	}

	public boolean has(T element) {
		return this.registry.containsKey(element.getId());
	}

	public Optional<T> get(String id) {
		return Optional.ofNullable(id)
				.map(this.registry::get);
	}

	public boolean add(T object) {
		return object != null && this.registry.putIfAbsent(object.getId(), object) == null;
	}

	public boolean remove(T element) {
		return this.registry.remove(element.getId()) != null;
	}

	public int size() {
		return this.registry.size();
	}

	public boolean isEmpty() {
		return this.registry.isEmpty();
	}

	@NotNull
	@Override
	public Iterator<T> iterator() {
		return registry.values().iterator();
	}

	public Stream<T> stream() {
		return registry.values().stream();
	}

}
