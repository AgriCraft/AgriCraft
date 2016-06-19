/*
 */
package com.infinityraider.agricraft.api.v3.fertiliser;

import java.util.Random;

/**
 * An interface for fertilizable things.
 *
 * @author RlonRyan
 */
public interface IFertilizable {

	/**
	 * Checks if a certain fertilizer may be applied to this crop
	 *
	 * @param fertiliser the fertilizer to be checked
	 * @return if the fertilizer may be applied
	 */
	boolean acceptsFertiliser(IAgriFertiliser fertiliser);

	/**
	 * Apply fertilizer to this crop. This method is deprecated since it makes
	 * no sense.
	 *
	 * @param fertiliser the fertilizer to be applied
	 * @return if the fertilizer was successfully applied.
	 */
	@Deprecated
	boolean applyFertiliser(IAgriFertiliser fertiliser, Random rand);

}
