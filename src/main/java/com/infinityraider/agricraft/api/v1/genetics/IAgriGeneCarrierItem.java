package com.infinityraider.agricraft.api.v1.genetics;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.util.IAgriItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatProvider;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Interface to be implemented in Item classes which can carry genes.
 * Examples are the AgriCraft IAgriSeedItem or IAgriTrowelItem
 */
public interface IAgriGeneCarrierItem extends IAgriItem {
    /**
     * Fetches the IAgriGenome from a stack
     * @param stack the stack
     * @return Optional holding the genome, or empty if invalid
     */
    @Nonnull
    Optional<IAgriGenome> getGenome(ItemStack stack);

    /**
     * Fetches the IAgriPlant from a stack
     * @param stack the stack
     * @return the plant, can be an invalid (IAgriPlant.isPlant() will return false) plant if invalid
     */
    default IAgriPlant getPlant(ItemStack stack) {
        return this.getGenome(stack).map(IAgriGenome::getPlant).orElse(AgriApi.getPlantRegistry().getNoPlant());
    }

    /**
     * Fetches the IAgriStatsMap from a stack
     * @param stack the stack
     * @return Optional holding the stats, or empty if invalid
     */
    default Optional<IAgriStatsMap> getStats(ItemStack stack) {
        return this.getGenome(stack).map(IAgriStatProvider::getStats);
    }
}
