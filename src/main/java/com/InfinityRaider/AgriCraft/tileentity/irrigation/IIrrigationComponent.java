
package com.InfinityRaider.AgriCraft.tileentity.irrigation;

/**
 * The base for all irrigation components in AgriCraft.
 * <p>
 * <i>Java 8 would make this so much easier, with default methods.</i>
 * <p>
 * @since 1.3
 */
public interface IIrrigationComponent {

	/**
	 * Sets the irrigation component's contained fluid level.
	 * Don't use this method to transfer fluids between containers.
	 * 
	 * @param lvl the level to set the fluid to.
	 */
	void setFluidLevel(int lvl);

	/**
	 * Retrieves the irrigation component's fluid level.
	 * 
	 * @return the component's fluid level.
	 */
	int getFluidLevel();
	
	/**
	 * Retrieves the irrigation component's maximum fluid level.
	 * 
	 * @return the component's maximum fluid level.
	 */
	int getCapacity();
	
	/**
	 * Determines the water height, relative to the bottom of the block, and scaled on the 0 - {@value Constants.WHOLE} interval.
	 * <p>
	 * <b>Notice</b>: This method is not to be considered efficient, due to the rounding required.
	 * </p>
	 * @return the component's fluid height.
	 */
	float getFluidHeight();

	/**
	 * Determines if a component may connect to another component.
	 * 
	 * @param component the component to connect to.
	 * @return if the component may connect to the given component.
	 */
	boolean canConnectTo(IIrrigationComponent component);
	
	/**
	 * Attempts to add fluid to an irrigation component.
	 * 
	 * @param amount the amount to add.
	 * @return the amount of fluid left over.
	 */
	//int pushFluid(int amount);
	
	/**
	 * Attempts to remove an amount of fluid from the irrigation component.
	 * 
	 * @param amount the amount of fluid desired to be removed.
	 * @return the amount of fluid <em>actually</em> removed.
	 */
	//int pullFluid(int amount);

}
