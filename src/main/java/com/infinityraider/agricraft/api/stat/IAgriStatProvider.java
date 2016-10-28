/*
 */
package com.infinityraider.agricraft.api.stat;

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
     * @return the stat associated with the instance or null.
     */
    IAgriStat getStat();

}
