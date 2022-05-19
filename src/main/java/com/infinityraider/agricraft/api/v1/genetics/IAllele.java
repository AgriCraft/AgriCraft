package com.infinityraider.agricraft.api.v1.genetics;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nonnull;

/**
 * Interface for the different alleles of a gene
 *
 * Implementations of IAllel should be bijective between their trait and themselves
 * @param <A> the type of the gene / allel
 */
public interface IAllele<A> {
    /**
     * @return the gene for which this is an allel
     */
    IAgriGene<A> gene();

    /**
     * @return the trait represented by this allel (it's "value")
     */
    A trait();

    /**
     * Checks if this allel is dominant over another allel
     * @param other an other allel of the same gene
     * @return true if this is the dominant trait (or if the alleles are equal)
     */
    boolean isDominant(IAllele<A> other);

    /**
     * @return A text component representing this allele in a tooltip
     */
    MutableComponent getTooltip();

    /**
     * @return value representing this allele relative to other alleles in the same gene, used in comparators
     */
    int comparatorValue();

    /**
     * Writes this allel to NBT
     * @return a CompoundNBT tag to which this allel has been serialized
     */
    @Nonnull
    CompoundTag writeToNBT();

    /**
     * Alleles should be defined unique per gene, therefore two alleles can only be equal if they are identical
     */
    default boolean equals(IAllele<A> other) {
        return this == other;
    }
}
