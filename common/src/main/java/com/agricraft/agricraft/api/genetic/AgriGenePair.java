package com.agricraft.agricraft.api.genetic;

/**
 * Gene Pair for two alleles of a gene in a genome
 * @param <T> the type of the gene
 */
public class AgriGenePair<T> {

	private final AgriGene<T> gene;
	private final AgriAllele<T> dominant;
	private final AgriAllele<T> recessive;

	public AgriGenePair(AgriGene<T> gene, AgriAllele<T> first, AgriAllele<T> second) {
		this.gene = gene;
		if (first.isDominant(second)) {
			this.dominant = first;
			this.recessive = second;
		} else {
			this.dominant = second;
			this.recessive = first;
		}
	}
	public AgriGenePair(AgriGene<T> gene, AgriAllele<T> both) {
		this.gene = gene;
		this.dominant = both;
		this.recessive = both;
	}

	/**
	 * @return the gene for this gene pair
	 */
	public AgriGene<T> getGene() {
		return this.gene;
	}

	/**
	 * @return The apparent trait resulting form the two alleles (by default this is the dominant allele)
	 */
	public T getTrait() {
		return this.getDominant().trait();
	}

	/**
	 * @return the dominant allele
	 */
	public final AgriAllele<T> getDominant() {
		return this.dominant;
	}

	/**
	 * @return the recessive allele
	 */
	public final AgriAllele<T> getRecessive() {
		return this.recessive;
	}

	public AgriGenePair<T> copy() {
		return new AgriGenePair<>(this.getGene(), this.getDominant(), this.getRecessive());
	}

}
