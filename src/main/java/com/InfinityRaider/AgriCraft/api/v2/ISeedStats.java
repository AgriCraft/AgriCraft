package com.InfinityRaider.AgriCraft.api.v2;

public interface ISeedStats extends com.InfinityRaider.AgriCraft.api.v1.ISeedStats {
    /**
     * @return The growth value of the seed.
     */
    @Override
    short getGrowth();

    /**
     * @return The gain value of the seed.
     */
    @Override
    short getGain();

    /**
     * @return The strength value of the seed.
     */
    @Override
    short getStrength();

    /**
     * @return The maximum growth value a seed of this kind can have.
     */
    @Override
    short getMaxGrowth();

    /**
     * @return The maximum gain value a seed of this kind can have.
     */
    @Override
    short getMaxGain();

    /**
     * @return The maximum strength value a seed of this kind can have.
     */
    @Override
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
