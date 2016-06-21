/*
 */
package com.infinityraider.agricraft.api.v1.plant;

import java.util.List;

/**
 * An interface for managing AgriCraft plants.
 *
 * @author AgriCraft Team
 */
public interface IPlantRegistry {
	
	boolean isPlant(IAgriPlant plant);
	
	IAgriPlant getPlant(String id);

	boolean addPlant(IAgriPlant plant);
	
	boolean removePlant(IAgriPlant plant);
	
	List<IAgriPlant> getPlants();
	
	List<String> getPlantIds();

}
