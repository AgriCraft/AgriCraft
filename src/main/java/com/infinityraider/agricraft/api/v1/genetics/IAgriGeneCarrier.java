package com.infinityraider.agricraft.api.v1.genetics;

/**
 * implemented in objects which hold IAgriGenomes
 */
public interface IAgriGeneCarrier {
    /**
     * Getter for the IAgriGenome
     * @return the genome
     */
    IAgriGenome getGenome();

    /**
     * Setter for the IAgriGenome
     * @param genome the genome
     */
    void setGenome(IAgriGenome genome);
}
