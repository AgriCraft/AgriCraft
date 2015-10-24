
package com.InfinityRaider.AgriCraft.tileentity.irrigation;

/**
 * The base for all irrigation components in AgriCraft.
 * <p>
 * <i>Java 8 would make this so much easier, with default methods.</i>
 * </p><p>
 * Should we just switch over to the Forge stuff?
 * </p>
 * @since 1.4.0
 */
public interface IIrrigationComponent {
	
	/**
	 * Determines if a component may connect to another component.
	 * 
	 * @param component the component to connect to.
	 * @return if the component may connect to the given component.
	 */
	boolean canConnectTo(IIrrigationComponent component);
	
	/**
	 * Determines if a component can accept fluid.
	 * e.g. if the component is not full.
	 * 
	 * @return if the component is accepting fluid.
	 */
	boolean canAccept();
	
	/**
	 * Determines if a component can provide fluid.
	 * e.g. if the component is not empty.
	 * 
	 * @return if the component can provide fluid.
	 */
	boolean canProvide();

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
	 * Sets the irrigation component's contained fluid level.
	 * Don't use this method to transfer fluids between containers.
	 * 
	 * @param lvl the level to set the fluid to.
	 */
	void setFluidLevel(int lvl);
	
	/**
	 * Attempts to add fluid to an irrigation component.
	 * 
	 * @param amount the amount to add.
	 * @return the amount of fluid left over.
	 */
	int pushFluid(int amount);
	
	/**
	 * Attempts to add fluid to an irrigation component.
	 * 
	 * @param amount the amount to add.
	 * @return the amount of fluid left over.
	 */
	//int pushFluid(int amount, int y);
	
	/**
	 * Attempts to remove an amount of fluid from the irrigation component.
	 * 
	 * @param amount the amount of fluid desired to be removed.
	 * @return the amount of fluid <em>actually</em> removed.
	 */
	int pullFluid(int amount);
	
	/**
	 * Attempts to remove an amount of fluid from the irrigation component.
	 * 
	 * @param amount the amount of fluid desired to be removed.
	 * @param y the y-level of the connection point, relative to the bottom of the block.
	 * @return the amount of fluid <em>actually</em> removed.
	 */
	//int pullFluid(int amount, int y);
	
	/**
	 * Synchronizes the fluid level between the server and it's clients.
	 * <p>
	 * Should not be called excessively, and implementations should use a discrete level system to prevent too frequent synchronization.
	 * </p>
	 */
	void syncFluidLevel();

}
