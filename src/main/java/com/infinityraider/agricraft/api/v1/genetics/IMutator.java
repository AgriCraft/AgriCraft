package com.infinityraider.agricraft.api.v1.genetics;

import net.minecraft.util.Tuple;

import java.util.Random;

/**
 * The IMutator object controls how alleles of a gene mutate through generations
 * @param <A> the type of the gene / mutator
 */
public interface IMutator<A> {
    /**
     * Combines two parent alleles into a new gene pair, possibly mutating one or both of these alleles to be passed on to offspring
     * @param gene the gene for the alleles
     * @param first one of the alleles for the gene of the first parent
     * @param second one of the alleles for the gene of the second parent
     * @param parents a tuple holding the full genomes of both parents
     * @param random pseudo-random generator to take decisions
     * @return a new gene pair for the offspring
     */
    IAgriGenePair<A> pickOrMutate(IAgriGene<A> gene, IAllel<A> first, IAllel<A> second, Tuple<IAgriGenome, IAgriGenome> parents, Random random);
}
