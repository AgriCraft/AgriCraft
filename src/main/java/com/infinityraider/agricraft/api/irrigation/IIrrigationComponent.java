package com.infinityraider.agricraft.api.irrigation;

/**
 * The root interface for all irrigation components in AgriCraft.
 *
 * @since 1.4.0
 */
public interface IIrrigationComponent extends IConnectable, IIrrigationContainer, IIrrigationAcceptor {

	/**
	 * Retrieves the irrigation component's maximum fluid level.
	 *
	 * @return the component's maximum fluid level.
	 */
	int getCapacity();

	/**
	 * Determines the water height, relative to the bottom of the block, and
	 * scaled on the 0 - 16 interval.
	 * <p>
	 * <b>Notice</b>: This method is not to be considered efficient, due to the
	 * rounding required.
	 * </p>
	 *
	 * @return the component's fluid height.
	 */
	float getFluidHeight(int lvl);

	/**
	 * Sets the irrigation component's contained fluid level. Don't use this
	 * method to transfer fluids between containers.
	 *
	 * @param lvl the level to set the fluid to.
	 */
	@Deprecated
	void setFluidLevel(int lvl);
	
	/**
	 * Synchronizes the fluid level between the server and it's clients.
	 * <p>
	 * Should not be called excessively, and implementations should use a
	 * discrete level system to prevent too frequent synchronization.
	 * </p>
	 */
	void syncFluidLevel();

}
