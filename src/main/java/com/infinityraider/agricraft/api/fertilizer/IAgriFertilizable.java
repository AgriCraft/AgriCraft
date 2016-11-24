/*
 */
package com.infinityraider.agricraft.api.fertilizer;

import java.util.Random;

/**
 * An interface for fertilizable things.
 *
 */
public interface IAgriFertilizable {

    /**
     * Checks if a certain fertilizer may be applied to this crop
     *
     * @param fertilizer the fertilizer to be checked
     * @return if the fertilizer may be applied
     */
    boolean acceptsFertilizer(IAgriFertilizer fertilizer);

    /**
     * Apply fertilizer to this crop.
     *
     * @param fertilizer the fertilizer to be applied.
     * @param rand the random number generator to be used.
     * @return if the fertilizer was successfully applied.
     */
    boolean onApplyFertilizer(IAgriFertilizer fertilizer, Random rand);

}
