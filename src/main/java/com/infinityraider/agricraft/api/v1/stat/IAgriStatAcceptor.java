/*
 */
package com.infinityraider.agricraft.api.v1.stat;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 *
 */
public interface IAgriStatAcceptor {

    /**
     * Determines if an AgriStat is valid for this specific instance.
     *
     * @param stat the stat to validate for the instance.
     * @return if the stat is valid for this instance.
     */
    boolean acceptsStat(@Nullable IAgriStat stat);

    /**
     * Sets the AgriStat associated with this instance. Should always return the same result as
     * acceptsStat() if the stat is invalid.
     *
     * @param stat the stat to associate with this instance.
     * @return if the stat was successfully associated with the instance.
     */
    boolean setStat(@Nullable IAgriStat stat);

    /**
     * Clears the AgriStat associated with the instance.
     *
     * @return the stat removed from the instance, or the empty optional.
     */
    @Nonnull
    Optional<IAgriStat> removeStat();

}
