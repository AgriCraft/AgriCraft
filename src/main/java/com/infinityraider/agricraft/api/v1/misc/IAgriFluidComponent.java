/*
 *
 */
package com.infinityraider.agricraft.api.v1.misc;

import javax.annotation.Nonnull;
import net.minecraft.util.EnumFacing;

/**
 *
 * @author Ryan
 */
public interface IAgriFluidComponent extends IAgriConnectable {

    /**
     * Retrieves the fluid amount in millibuckets.
     *
     * @return the amount of fluid in millibuckets.
     */
    public int getFluidAmount();

    /**
     * Retrieves the height of the fluid in the component, in microblocks.
     *
     * @return the height of the fluid in microblocks.
     */
    public int getFluidHeight();

    /**
     * Retrieves the minimum height, in microblocks, that the component supports.
     *
     * @return the minimum fluid height.
     */
    public int getMinFluidHeight();

    /**
     * Retrieves the maximum height, in microblocks, that the component supports.
     *
     * @return the maximum fluid height.
     */
    public int getMaxFluidHeight();

    /**
     * Retrieves the component's maximum fluid capacity, in millibuckets.
     *
     * @return the capacity, in millibuckets.
     */
    public int getFluidCapacity();

    /**
     * Pushes fluid into the component at the given height, returning the amount not consumed.
     *
     * @param inputHeight the height at which to push the fluid into the component.
     * @param inputAmount the amount of fluid to push into the component.
     * @param partial if the component should accept only part of the given amount.
     * @param simulate if the component should actually update the amount of fluid, or not.
     * @return the amount of fluid that was not accepted by the component.
     */
    public int acceptFluid(int inputHeight, int inputAmount, boolean partial, boolean simulate);

    /**
     * Retrieves the cached fluid level for neighbors to this tile. Returns -1 if the side is not in
     * the cache.
     *
     * @param side the side to get the cached fluid level for.
     * @return the last known fluid level for the neighboring component on the given side, or -1.
     */
    public default int getCachedFluidHeightFor(@Nonnull EnumFacing side) {
        return -1;
    }

}
