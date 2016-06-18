/*
 */
package com.infinityraider.agricraft.api.v3.registry;

import java.util.List;
import net.minecraft.item.ItemStack;
import com.infinityraider.agricraft.api.v3.core.IAgriPlant;

/**
 * An interface for managing AgriCraft plants.
 *
 * @author AgriCraft Team
 */
public interface IPlantRegistry {
	
	boolean isPlant(ItemStack seed);
	
	boolean isPlant(IAgriPlant plant);

	boolean addPlant(IAgriPlant plant);
	
	IAgriPlant getPlant(ItemStack seed);
	
	List<IAgriPlant> getPlants();
	
	List<String> getPlantIds();

}
