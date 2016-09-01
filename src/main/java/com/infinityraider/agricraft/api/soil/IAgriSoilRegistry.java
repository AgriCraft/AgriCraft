/*
 */
package com.infinityraider.agricraft.api.soil;

import java.util.List;

/**
 * An interface for managing AgriCraft plants.
 *
 * @author AgriCraft Team
 */
public interface IAgriSoilRegistry {
	
	boolean isSoil(IAgriSoil plant);
	
	IAgriSoil getSoil(String id);

	boolean addSoil(IAgriSoil plant);
	
	boolean removeSoil(IAgriSoil plant);
	
	List<IAgriSoil> getSoils();
	
	List<String> getSoilIds();

}
