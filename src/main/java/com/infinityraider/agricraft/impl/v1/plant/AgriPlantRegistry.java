package com.infinityraider.agricraft.impl.v1.plant;

import com.agricraft.agricore.registry.AgriPlants;
import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.misc.IAgriRegistry;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class AgriPlantRegistry extends AgriPlants implements IAgriRegistry<IAgriPlant> {
    public static final String NAME = "plants";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Class<IAgriPlant> clazz() {
        return IAgriPlant.class;
    }

    @Override
    public boolean has(String id) {
        return this.hasPlant(id);
    }

    @Override
    public boolean has(@Nullable IAgriPlant element) {
        return false;
    }

    @Override
    public Optional<IAgriPlant> get(String id) {
        return Optional.empty();
    }

    @Override
    public boolean add(@Nullable IAgriPlant element) {
        return false;
    }

    @Override
    public boolean remove(@Nullable IAgriPlant element) {
        return false;
    }

    @Nonnull
    @Override
    public Collection<IAgriPlant> all() {
        return null;
    }

    @Nonnull
    @Override
    public Set<String> ids() {
        return null;
    }

    @Nonnull
    @Override
    public Stream<IAgriPlant> stream() {
        return null;
    }

    @Override
    public ImmutableSet<IAgriPlant> getAll() {
        return null;
    }
}
