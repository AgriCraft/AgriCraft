package com.infinityraider.agricraft.api.v1;

public interface ISeedStats {
    /**
     * @return The growth value of the seed.
     */
    short getGrowth();

    /**
     * @return The gain value of the seed.
     */
    short getGain();

    /**
     * @return The strength value of the seed.
     */
    short getStrength();

    /**
     * @return The maximum growth value a seed of this kind can have.
     */
    short getMaxGrowth();

    /**
     * @return The maximum gain value a seed of this kind can have.
     */
    short getMaxGain();

    /**
     * @return The maximum strength value a seed of this kind can have.
     */
    short getMaxStrength();

    /**
     * @return if the seed stats are analyzed
     */
    boolean isAnalyzed();

    /**
     * Sets if the stats are analyzed
     * @param value to be set
     */
    void setAnalyzed(boolean value);
}
