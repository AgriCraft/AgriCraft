package com.agricraft.agricraft.api.genetics;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * A class for objects containing seeds.
 */
public interface IAgriGenomeProvider {

	/**
	 * Determines if the object currently has an associated genome.
	 *
	 * @return if the object has a plant associated with it.
	 */
	boolean hasGenome();

	/**
	 * Getter for the IAgriGenome
	 *
	 * @return optional containing the genome, or empty if nothing is planted
	 */
	@NotNull
	Optional<IAgriGenome> getGenome();

}
