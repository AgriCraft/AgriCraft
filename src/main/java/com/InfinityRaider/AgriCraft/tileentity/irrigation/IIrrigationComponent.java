
package com.InfinityRaider.AgriCraft.tileentity.irrigation;

/**
 * The base for all irrigation components in AgriCraft.
 */
public interface IIrrigationComponent {

	/**
	 * Sets the irrigation component's contained fluid level.
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
	 * Determines if a component may connect to another component.
	 * 
	 * @param component the component to connect to.
	 * @return if the component may connect to the given component.
	 */
	boolean canConnectTo(IIrrigationComponent component);

}
