package com.infinityraider.agricraft.api.v1.genetics;

import com.infinityraider.agricraft.api.v1.util.IAgriRegisterable;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.IFormattableTextComponent;

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
     *
     * @param plant the plant for which to fetch the default allel
     * @return the default allel for this gene
     */
    @Nonnull
    IAllele<A> defaultAllele(IAgriPlant plant);

    /**
     * Maps a value of the gene to an allel, it is possible that this value falls out of the set of acceptable values,
     * in which case a different allel must be returned (e.g. the default, or the closest one)
     *
     * @param value the value
     * @return the allel for a value
     */
    @Nonnull
    IAllele<A> getAllele(A value);

    /**
     * Used when deserializing genomes, reads an allel from NBT.
     * The serialiazation methods is defined on the IAllel class
     *
     * @param tag the CompoundNBT tag
     * @return the allel
     */
    @Nonnull
    IAllele<A> readAlleleFromNBT(@Nonnull CompoundNBT tag);

    /**
     * @return the set of all allowed alleles for this gene.
     */
    @Nonnull
    Set<IAllele<A>> allAlleles();

    /**
     * @return The mutator object which controls mutations for this gene
     */
    @Nonnull
    IMutator<A> mutator();

    /**
     * Generates a gene pair for this gene based on two alleles
     * @param first the first allel
     * @param second the second allel
     * @return gene pair for this gene for the two alleles
     */
    @Nonnull
    IAgriGenePair<A> generateGenePair(IAllele<A> first, IAllele<A> second);

    /**
     * @return an ITextComponent to describe this gene on the client
     */
    @Nonnull
    IFormattableTextComponent getGeneDescription();

    /**
     * @return the RGB values to color the dominant part of the DNA helix for this gene, only used client side
     */
    @Nonnull
    Vector3f getDominantColor();

    /**
     * @return the RGB values to color the recessive part of the DNA helix for this gene, only used client side
     */
    @Nonnull
    Vector3f getRecessiveColor();

    /**
     * Gets the weight of the alleles of this gene in the genome, used in genome comparators.
     * The comparator values returned from the alleles in this gene will be multiplied with this value.
     *
     * For reference, the stat gene weight is 10.
     *
     * @return the weight
     */
    int getComparatorWeight();

    /**
     * AgriCraft provides the possibility to have genes completely hidden.
     * Hidden genes will not be revealed in any regular gameplay mechanics (for instance the seed analyzer).
     *
     * Note that hidden genes will be visible if someone analyzes the nbt data of the ItemStacks though.
     *
     * @return true if this is a hidden gene
     */
    default boolean isHidden() {
        return false;
    }
}
