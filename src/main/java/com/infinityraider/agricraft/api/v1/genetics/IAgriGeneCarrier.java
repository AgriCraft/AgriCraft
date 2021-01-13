package com.infinityraider.agricraft.api.v1.genetics;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * implemented in objects which hold IAgriGenomes
 */
public interface IAgriGeneCarrier {
    /**
     * Getter for the IAgriGenome
     * @return the genome
     */
    @Nonnull
    Optional<IAgriGenome> getGenome();

    /**
     * Setter for the IAgriGenome
     * @param genome the genome
     */
    void setGenome(@Nonnull IAgriGenome genome);
}
