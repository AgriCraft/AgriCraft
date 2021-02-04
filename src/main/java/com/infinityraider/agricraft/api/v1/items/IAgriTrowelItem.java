package com.infinityraider.agricraft.api.v1.items;

import com.infinityraider.agricraft.api.v1.crop.IAgriGrowthStage;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import net.minecraft.item.ItemStack;

import java.util.Optional;

/**
 * Marker interface for trowel items.
 */
public interface IAgriTrowelItem {
    /**
     * Checks if the trowel is carrying a plant
     * @param stack the stack
     * @return true if the trowel is carrying a plant
     */
    boolean hasPlant(ItemStack stack);

    /**
     * Sets the plant on the trowel
     * @param stack the stack
     * @param genome the genome of the plant
     * @param stage the growth stage of the plant
     * @return true if success (can fail if the trowel is already carrying a plant)
     */
    boolean setPlant(ItemStack stack, IAgriGenome genome, IAgriGrowthStage stage);


    /**
     * Removes the plant from the trowel
     * @param stack the stack
     * @return true if success (can fail if the trowel was not carrying a plant)
     */
    boolean removePlant(ItemStack stack);

    /**
     * Fetches the IAgriGenome from a stack
     * @param stack the stack
     * @return Optional holding the genome, or empty if invalid
     */
    Optional<IAgriGenome> getGenome(ItemStack stack);


    /**
     * Fetches the IAgriGrowthStage from a stack
     * @param stack the stack
     * @return Optional holding the growth stage, or empty if invalid
     */
    Optional<IAgriGrowthStage> getGrowthStage(ItemStack stack);

}
