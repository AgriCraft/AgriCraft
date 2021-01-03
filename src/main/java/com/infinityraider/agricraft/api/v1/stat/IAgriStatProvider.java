package com.infinityraider.agricraft.api.v1.stat;

import javax.annotation.Nonnull;

/**
 * Interface for objects that have AgriStats.
 */
public interface IAgriStatProvider {
    @Nonnull
    IAgriStatsMap getStats();

}
