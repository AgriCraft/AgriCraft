/*
 */
package com.infinityraider.agricraft.api.v1.misc;

import java.util.function.Consumer;
import javax.annotation.Nonnull;

/**
 * Interface for providing players information from AgriCraft. This will be used to display
 * information in Waila for crops.
 *
 *
 */
public interface IAgriDisplayable {

    /**
     * Retrieves information for display to the player.
     *
     * @param consumer the list to add the display information to.
     */
    public void addDisplayInfo(@Nonnull Consumer<String> consumer);

}
