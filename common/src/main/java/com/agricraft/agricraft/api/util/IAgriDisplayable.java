package com.agricraft.agricraft.api.util;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Interface for providing players information from AgriCraft. This will be used to display
 * information in Waila for crops.
 */
public interface IAgriDisplayable {

	/**
	 * Retrieves information for display to the player.
	 *
	 * @param consumer the list to add the display information to.
	 */
	void addDisplayInfo(@NotNull Consumer<Component> consumer);

}
