/*
 */
package com.infinityraider.agricraft.api.irrigation;

import net.minecraft.util.EnumFacing;

/**
 * Root interface for all connectable blocks.
 * 
 * @author RlonRyan
 */
public interface IConnectable {
	
	/**
	 * Determines if a component may connect to another component.
	 * 
	 * @param side the side of the component to connect on.
	 * @param connectable the component wishing to connect to this component.
	 * @return if the component may connect.
	 */
	boolean canConnectTo(EnumFacing side, IConnectable connectable);
	
}
