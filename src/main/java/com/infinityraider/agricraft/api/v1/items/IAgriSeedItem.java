package com.infinityraider.agricraft.api.v1.items;

import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import net.minecraft.item.ItemStack;

import java.util.Optional;

/**
 * Implemented in the AgriCraft seed Item object
 * To obtain, check if an ItemStack's item is an instance of this class and cast.
 */
public interface IAgriSeedItem {
    /**
     * Fetches the IAgriGenome from a stack
     * @param stack the stack
     * @return Optional holding the genome, or empty if invalid
     */
    Optional<IAgriGenome> getGenome(ItemStack stack);

    /**
     * Fetches the AgriSeed from a stack
     * @param stack the stack
     * @return Optional holding the seed, or empty if invalid
     */
    Optional<AgriSeed> getSeed(ItemStack stack);

    /**
     * Fetches the IAgriPlant from a stack
     * @param stack the stack
     * @return the plant, can be an invalid (IAgriPlant.isPlant() will return false) plant if invalid
     */
    IAgriPlant getPlant(ItemStack stack);

    /**
     * Fetches the IAgriStatsMap from a stack
     * @param stack the stack
     * @return Optional holding the stats, or empty if invalid
     */
    Optional<IAgriStatsMap> getStats(ItemStack stack);
}
