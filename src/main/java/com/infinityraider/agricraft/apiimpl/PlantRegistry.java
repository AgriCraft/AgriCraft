/*
 */
package com.infinityraider.agricraft.apiimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.plant.IAgriPlantRegistry;

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
    public List<IAgriPlant> getPlants() {
        return new ArrayList<>(this.plants.values());
    }

    @Override
    public List<String> getPlantIds() {
        return new ArrayList<>(this.plants.keySet());
    }

}
