/*
 */
package com.infinityraider.agricraft.api.soil;

import java.util.Collection;

import net.minecraft.item.ItemStack;

import com.infinityraider.agricraft.api.util.FuzzyStack;

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
    Collection<FuzzyStack> getVarients();

    default boolean isVarient(ItemStack stack) {
        return stack != null && isVarient(new FuzzyStack(stack));
    }

    default boolean isVarient(FuzzyStack stack) {
        return this.getVarients().contains(stack);
    }

}
