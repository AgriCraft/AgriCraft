/*
 */
package com.infinityraider.agricraft.api.v1.fertiliser;

import java.util.Random;

/**
 * An interface for fertilizable things.
 *
 * @author RlonRyan
 */
public interface IFertilisable {
	
	/**
	 * Determines if the crop may be fertilized using bonemeal.
	 * Planned to be replaced by wrapping bonemeal as a fertiliser in the fertiliser registry.
	 * 
	 * @return if bonemeal can be applied to this crop.
	 */
	@Deprecated
	boolean canBonemeal();

	/**
	 * Checks if a certain fertiliser may be applied to this crop
	 *
	 * @param fertiliser the fertiliser to be checked
	 * @return if the fertiliser may be applied
	 */
	boolean acceptsFertiliser(IAgriFertiliser fertiliser);

	/**
	 * Apply fertiliser to this crop. This method is deprecated since it makes
	 * no sense.
	 *
	 * @param fertiliser the fertiliser to be applied
	 * @return if the fertiliser was successfully applied.
	 */
	@Deprecated
	boolean applyFertiliser(IAgriFertiliser fertiliser, Random rand);

}
