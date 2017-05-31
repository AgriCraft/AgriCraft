/*
 */
package com.infinityraider.agricraft.api.v1.irrigation;

/**
 *
 *
 */
public interface IIrrigationContainer {

    /**
     * Determines if the component contains an amount of fluid.
     *
     * @return if the component contains a fluid.
     */
    default boolean hasFluid() {
        return getFluidAmount(0) != 0;
    }

    /**
     * Determines the amount of fluid above a certain y-level.
     *
     * @param y the y-level to check above.
     * @return the amount of fluid above the y-level.
     */
    int getFluidAmount(int y);

    /**
     * Determines the y-level of the fluid in the container.
     *
     * @return
     */
    int getFluidHeight();

}
