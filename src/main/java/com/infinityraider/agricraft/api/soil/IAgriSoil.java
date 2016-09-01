/*
 */
package com.infinityraider.agricraft.api.soil;

import java.util.Collection;
import net.minecraft.item.ItemStack;

/**
 * Class for interacting with AgriCraft soil definitions.
 */
public interface IAgriSoil {
    
    String getId();
    
    String getName();
    
    /**
     * Returns an ItemStack representative of this AgriSoil.
     * @return an ItemStack representing this soil.
     */
    Collection<ItemStack> getVarients();
    
}
