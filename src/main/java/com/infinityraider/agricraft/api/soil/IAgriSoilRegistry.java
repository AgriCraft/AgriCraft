/*
 */
package com.infinityraider.agricraft.api.soil;

import com.infinityraider.agricraft.api.util.FuzzyStack;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

/**
 * An interface for managing AgriCraft plants.
 *
 * @author AgriCraft Team
 */
public interface IAgriSoilRegistry {
	
	boolean isSoil(IAgriSoil plant);
	
	Optional<IAgriSoil> getSoil(String id);
    
    default Optional<IAgriSoil> getSoil(IBlockState state) {
        return FuzzyStack.fromBlockState(state).flatMap(this::getSoil);
    }
    
    default Optional<IAgriSoil> getSoil(ItemStack stack) {
        return this.getSoil(new FuzzyStack(stack));
    }
    
    default Optional<IAgriSoil> getSoil(FuzzyStack stack) {
        return this.getSoils().stream()
                .filter(s -> s.isVarient(stack))
                .findFirst();
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
