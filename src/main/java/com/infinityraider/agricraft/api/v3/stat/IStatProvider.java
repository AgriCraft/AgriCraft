/*
 */
package com.infinityraider.agricraft.api.v3.stat;

/**
 * Interface for objects that have AgriStats.
 *
 * @author RlonRyan
 */
public interface IStatProvider {

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
