/*
 */
package com.infinityraider.agricraft.api.v3.registry;

import com.infinityraider.agricraft.api.v3.IAgriCraftPlant;
import java.util.List;
import net.minecraft.item.ItemStack;

/**
 * An interface for managing AgriCraft plants.
 *
 * @author AgriCraft Team
 */
public interface IPlantRegistry {
	
	boolean isPlant(ItemStack seed);
	
	boolean isPlant(IAgriCraftPlant plant);

	boolean addPlant(IAgriCraftPlant plant);
	
	IAgriCraftPlant getPlant(ItemStack seed);
	
	List<IAgriCraftPlant> getPlants();
	
	List<String> getPlantIds();

}
