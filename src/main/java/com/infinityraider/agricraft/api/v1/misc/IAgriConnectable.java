/*
 */
package com.infinityraider.agricraft.api.v1.misc;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;

/**
 * Root interface for all connectable blocks.
 */
public interface IAgriConnectable {

    /**
     * Determines if a component may connect to another component.
     *
     * @param side the side of the component to connect on.
     * @param connectable the component wishing to connect to this component.
     * @return if the component may connect.
     */
    boolean canConnectTo(@Nonnull Direction side, @Nonnull IAgriConnectable connectable);

    /**
     * Retrieves a copy of the connection matrix for the connectable.
     * 
     * @return a copy of the connection matrix.
     */
    @Nonnull
    AgriSideMetaMatrix getConnections();

    /**
     * Called whenever the component should refresh its connections table.
     * Default implementation does nothing.
     */
    default void refreshConnections() {
        // Do nothing.
    }

}
