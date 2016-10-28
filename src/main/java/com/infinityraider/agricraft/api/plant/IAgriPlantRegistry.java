/*
 */
package com.infinityraider.agricraft.api.plant;

import java.util.List;

/**
 * An interface for managing AgriCraft plants.
 *
 * @author AgriCraft Team
 */
public interface IAgriPlantRegistry {

    boolean isPlant(IAgriPlant plant);

    IAgriPlant getPlant(String id);

    boolean addPlant(IAgriPlant plant);

    boolean removePlant(IAgriPlant plant);

    List<IAgriPlant> getPlants();

    List<String> getPlantIds();

}
