package com.agricraft.agricraft.api.genetic;

/**
 * Interface for the different alleles of a gene
 * <p>
 * Implementations of AgriAllele should be bijective between their trait and themselves
 * @param <T> the type of the gene / allele
 */
public interface AgriAllele<T> {

	/**
	 * @return the trait represented by this allel (it's "value")
	 */
	T trait();

	/**
	 * Checks if this allele is dominant over another allele
	 * @param other another allele of the same gene
	 * @return true if this is the dominant trait (or if the alleles are equal)
	 */
	boolean isDominant(AgriAllele<T> other);

	/**
	 * @return the gene for which this is an allele
	 */
	AgriGene<T> gene();

}
