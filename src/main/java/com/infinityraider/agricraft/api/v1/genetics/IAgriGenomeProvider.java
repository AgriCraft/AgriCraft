package com.infinityraider.agricraft.api.v1.genetics;

import javax.annotation.Nonnull;
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
     * @return optional containing the genome, or empty if nothing is planted
     */
    @Nonnull
    Optional<IAgriGenome> getGenome();
}
