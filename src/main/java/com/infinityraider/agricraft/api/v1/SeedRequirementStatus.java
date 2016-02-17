package com.InfinityRaider.AgriCraft.api.v1;

public enum SeedRequirementStatus {
	/**
	 * The seed can be planted here.
	 */
	CAN_APPLY,
	/**
	 * The seed cannot be planted on this block.
	 */
	WRONG_SOIL,
	/**
	 * The seed has requirements that are not met or there are other reasons it
	 * cannot be planted.
	 */
	MISSING_REQUIREMENTS,
	/**
	 * The seed could be planted if the block was tilled.
	 * 
	 * <p>
	 * Note: For soils other than vanilla dirt/farmland, WRONG_SOIL may be
	 * returned instead.
	 * <P>
	 */
	NEEDS_TILLING,
	/**
	 * The given item is no seed.
	 */
	BAD_SEED,
	/**
	 * There's something wrong with the location, not the seed.
	 */
	BAD_LOCATION;
}
