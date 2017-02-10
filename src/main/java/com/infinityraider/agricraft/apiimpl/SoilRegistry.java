/*
 */
package com.infinityraider.agricraft.apiimpl;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import com.infinityraider.agricraft.api.soil.IAgriSoil;
import com.infinityraider.agricraft.api.soil.IAgriSoilRegistry;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 *
 *
 */
public class SoilRegistry implements IAgriSoilRegistry {

    private static final IAgriSoilRegistry INSTANCE = new SoilRegistry();

    private final ConcurrentMap<String, IAgriSoil> soils;

    public SoilRegistry() {
        this.soils = new ConcurrentHashMap<>();
    }

    public static IAgriSoilRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isSoil(IAgriSoil soil) {
        return this.soils.containsKey(soil.getId());
    }

    @Override
    public Optional<IAgriSoil> getSoil(String id) {
        return Optional.ofNullable(this.soils.get(id));
    }

    @Override
    public boolean addSoil(IAgriSoil soil) {
        return this.soils.putIfAbsent(soil.getId(), soil) == null;
    }

    @Override
    public boolean removeSoil(IAgriSoil soil) {
        return this.soils.remove(soil.getId()) != null;
    }

    @Override
    public Collection<IAgriSoil> getSoils() {
        return Collections.unmodifiableCollection(this.soils.values());
    }

    @Override
    public Set<String> getSoilIds() {
        return Collections.unmodifiableSet(this.soils.keySet());
    }

}
