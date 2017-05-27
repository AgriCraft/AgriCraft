/*
 */
package com.infinityraider.agricraft.apiimpl;

import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.plant.IAgriPlantRegistry;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 *
 */
public class PlantRegistry implements IAgriPlantRegistry {

    private static final IAgriPlantRegistry INSTANCE = new PlantRegistry();

    private final ConcurrentMap<String, IAgriPlant> plants;

    public PlantRegistry() {
        this.plants = new ConcurrentHashMap<>();
    }

    public static IAgriPlantRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isPlant(IAgriPlant plant) {
        return this.plants.containsKey(plant.getId());
    }

    @Override
    public IAgriPlant getPlant(String id) {
        return this.plants.get(id);
    }

    @Override
    public boolean addPlant(IAgriPlant plant) {
        return this.plants.putIfAbsent(plant.getId(), plant) == null;
    }

    @Override
    public boolean removePlant(IAgriPlant plant) {
        return this.plants.remove(plant.getId()) != null;
    }

    @Override
    public Collection<IAgriPlant> getPlants() {
        return Collections.unmodifiableCollection(this.plants.values());
    }

    @Override
    public Set<String> getPlantIds() {
        return Collections.unmodifiableSet(this.plants.keySet());
    }

}
