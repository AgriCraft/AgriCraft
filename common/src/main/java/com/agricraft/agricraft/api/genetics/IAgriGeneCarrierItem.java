package com.agricraft.agricraft.api.genetics;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.plant.IAgriPlant;
import com.agricraft.agricraft.api.stat.IAgriStatProvider;
import com.agricraft.agricraft.api.stat.IAgriStatsMap;
import com.agricraft.agricraft.api.util.IAgriItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Interface to be implemented in Item classes which can carry genes.
 * Examples are the AgriCraft IAgriSeedItem or IAgriTrowelItem
 */
public interface IAgriGeneCarrierItem extends IAgriItem {

	/**
	 * Fetches the IAgriGenome from a stack
	 *
	 * @param stack the stack
	 * @return Optional holding the genome, or empty if invalid
	 */
	@NotNull
	Optional<IAgriGenome> getGenome(ItemStack stack);

	/**
	 * Fetches the IAgriPlant from a stack
	 *
	 * @param stack the stack
	 * @return the plant, can be an invalid (IAgriPlant.isPlant() will return false) plant if invalid
	 */
	default IAgriPlant getPlant(ItemStack stack) {
		return this.getGenome(stack).map(IAgriGenome::getPlant).orElse(AgriApi.getPlantRegistry().getNoPlant());
	}

	/**
	 * Fetches the IAgriStatsMap from a stack
	 *
	 * @param stack the stack
	 * @return Optional holding the stats, or empty if invalid
	 */
	default Optional<IAgriStatsMap> getStats(ItemStack stack) {
		return this.getGenome(stack).map(IAgriStatProvider::getStats);
	}

}
