package com.agricraft.agricraft.api.genetic;

import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface AgriGenomeProviderItem {

	/**
	 * Change the genome of the plant.
	 * @param genome the new genome of the crop
	 */
	default void setGenome(ItemStack stack, AgriGenome genome) {
		genome.writeToNBT(stack.getOrCreateTag());
	}

	default Optional<AgriGenome> getGenome(ItemStack stack) {
		return Optional.ofNullable(AgriGenome.fromNBT(stack.getOrCreateTag()));
	}

}
