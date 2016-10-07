/*
 */
package com.infinityraider.agricraft.api.misc;

import java.util.List;

/**
 * Interface for providing players information from AgriCraft.
 * This will be used to display information in Waila for crops.
 *
 * 
 */
public interface IAgriDisplayable {
	
	/**
	 * Retrieves information for display to the player.
	 * 
	 * @param lines the list to add the display information to.
	 */
	public void addDisplayInfo(List<String> lines);
	
}
