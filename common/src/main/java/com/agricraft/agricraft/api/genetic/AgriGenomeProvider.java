package com.agricraft.agricraft.api.genetic;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface AgriGenomeProvider {

	/**
	 * Plant the genome on itself.
	 * This method is associated semantically with a planting action.
	 * @param genome the new genome of the crop
	 */
	default void plantGenome(AgriGenome genome) {
		this.plantGenome(genome, null);
	}

	/**
	 * Plant the genome on itself.
	 * This method is associated semantically with a planting action.
	 * @param genome the new genome of the crop
	 * @param entity the entity who planted the genome
	 */
	void plantGenome(AgriGenome genome, @Nullable LivingEntity entity);

	boolean removeGenome();

	AgriGenome getGenome();

}
