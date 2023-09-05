package com.agricraft.agricraft.api.genetic;

public interface AgriGenomeCarrier {

	/**
	 * Change the genome of the plant.
	 * @param genome the new genome of the crop
	 */
	void setGenome(AgriGenome genome);

	AgriGenome getGenome();

}
