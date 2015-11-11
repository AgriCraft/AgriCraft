package com.InfinityRaider.AgriCraft.api.v2;

public interface ISeedStats extends com.InfinityRaider.AgriCraft.api.v1.ISeedStats {
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
