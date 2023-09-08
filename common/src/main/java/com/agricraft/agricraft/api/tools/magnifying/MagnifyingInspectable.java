package com.agricraft.agricraft.api.tools.magnifying;

import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Interface to add components to the magnifying tooltip
 */
public interface MagnifyingInspectable {

	/**
	 * Add components to the tooltip that will be rendered
	 *
	 * @param tooltip          the list of component to add to
	 * @param isPlayerSneaking if the player is sneaking
	 */
	void addMagnifyingTooltip(List<Component> tooltip, boolean isPlayerSneaking);

}
