package com.agricraft.agricraft.api.genetic;

import com.agricraft.agricraft.api.crop.AgriCrop;
import net.minecraft.util.RandomSource;

/**
 * The AgriGeneMutator object controls how alleles of a gene mutate through generations
 *
 * @param <T> the type of the gene / mutator
 */
public interface AgriGeneMutator<T> {

	/**
	 * Combines two parent alleles into a new gene pair, possibly mutating one or both of these alleles to be passed on to offspring
	 *
	 * @param crop    the crop on which the cross-breed is occurring
	 * @param gene    the gene for the alleles
	 * @param first   one of the alleles for the gene of the first parent
	 * @param second  one of the alleles for the gene of the second parent
	 * @param parent1 the full genomes of the first parent1
	 * @param parent2 the full genomes of the first parent2
	 * @param random  pseudo-random generator to take decisions
	 * @return a new gene pair for the offspring
	 */
	AgriGenePair<T> pickOrMutate(AgriCrop crop, AgriGene<T> gene, AgriAllele<T> first, AgriAllele<T> second, AgriGenome parent1, AgriGenome parent2, RandomSource random);

}
