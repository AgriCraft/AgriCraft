/*
 */
package com.infinityraider.agricraft.api.soil;

import com.infinityraider.agricraft.api.util.FuzzyStack;
import java.util.List;

/**
 * An interface for managing AgriCraft plants.
 *
 * @author AgriCraft Team
 */
public interface IAgriSoilRegistry {
	
	boolean isSoil(IAgriSoil plant);
	
	IAgriSoil getSoil(String id);
    
    default IAgriSoil getSoil(FuzzyStack stack) {
        return this.getSoils().stream()
                .filter(s -> s.isVarient(stack))
                .findFirst()
                .orElse(null);
    }

	boolean addSoil(IAgriSoil plant);
	
	boolean removeSoil(IAgriSoil plant);
	
	List<IAgriSoil> getSoils();
	
	List<String> getSoilIds();
    
    default boolean isSoil(FuzzyStack soil) {
        return this.getSoils().stream()
                .anyMatch(s -> s.isVarient(soil));
    }

}
