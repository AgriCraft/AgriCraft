package com.infinityraider.agricraft.api.v1.genetics;

import com.infinityraider.agricraft.api.v1.misc.IAgriRegisterable;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Defines a gene from AgriCraft's genome for plants and seeds
 * Must be registered in the IAgriGeneRegistry to function correctly.
 *
 * @param <A> the type of the trait governed by this gene
 */
public interface IAgriGene<A> extends IAgriRegisterable<IAgriGene<?>> {
    /**
     * Gets the default fallback trait for the gene, used when genomes are constructed without explicitly assigning alleles
     * @return the default allel for this gene
     */
    @Nonnull
    IAllel<A> defaultAllel();

    /**
     * Maps a value of the gene to an allel, it is possible that this value falls out of the set of acceptable values,
     * in which case a different allel must be returned (e.g. the default, or the closest one)
     *
     * @param value the value
     * @return the allel for a value
     */
    @Nonnull
    IAllel<A> getAllel(A value);

    /**
     * Used when deserializing genomes, reads an allel from NBT.
     * The serialiazation methods is defined on the IAllel class
     *
     * @param tag the CompoundNBT tag
     * @return the allel
     */
    @Nonnull
    IAllel<A> readAllelFromNBT(@Nonnull CompoundNBT tag);

    /**
     * @return the set of all allowed alleles for this gene.
     */
    @Nonnull
    Set<IAllel<A>> allAlleles();

    /**
     * @return The mutator object which controls mutations for this gene
     */
    IMutator<A> mutator();

    /**
     * Generates a gene pair for this gene based on two alleles
     * @param first the first allel
     * @param second the second allel
     * @return gene pair for this gene for the two alleles
     */
    IAgriGenePair<A> generateGenePair(IAllel<A> first, IAllel<A> second);
}
