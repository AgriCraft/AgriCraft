/*
 */
package com.infinityraider.agricraft.apiimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.infinityraider.agricraft.api.soil.IAgriSoil;
import com.infinityraider.agricraft.api.soil.IAgriSoilRegistry;

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
    public boolean isSoil(IAgriSoil plant) {
        return this.soils.containsKey(plant.getId());
    }

    @Override
    public Optional<IAgriSoil> getSoil(String id) {
        return Optional.ofNullable(this.soils.get(id));
    }

    @Override
    public boolean addSoil(IAgriSoil plant) {
        return this.soils.putIfAbsent(plant.getId(), plant) == null;
    }

    @Override
    public boolean removeSoil(IAgriSoil plant) {
        return this.soils.remove(plant.getId()) != null;
    }

    @Override
    public List<IAgriSoil> getSoils() {
        return new ArrayList<>(this.soils.values());
    }

    @Override
    public List<String> getSoilIds() {
        return new ArrayList<>(this.soils.keySet());
    }

}
