/*
 */
package com.infinityraider.agricraft.api.v1.stat;

import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * Interface for objects that have AgriStats.
 *
 *
 */
public interface IAgriStatProvider {

    /**
     * Determines if the object currently has an associated AgriStat.
     *
     * @return if the object has a stat associated with it.
     */
    boolean hasStat();

    /**
     * Retrieves the AgriStat associated with this instance.
     *
     * @return the stat associated with the instance or the empty optional.
     */
    @Nonnull
    Optional<IAgriStat> getStat();

}
