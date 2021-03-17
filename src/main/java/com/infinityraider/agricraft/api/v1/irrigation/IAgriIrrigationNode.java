package com.infinityraider.agricraft.api.v1.irrigation;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;

public interface IAgriIrrigationNode {
    /**
     * @return the minimum fluid level, relative to the world coordinates (corresponds to an empty capacity)
     */
    double getMinFluidHeight();

    /**
     * @return the maximum fluid level, relative to the world coordinates (corresponds to a full capacity)
     */
    double getMaxFluidHeight();

    /**
     * Fetches the volume of fluid for a given fluid height, relative to the world coordinates
     * @param fluidHeight the fluid height
     * @return the volume of fluid in mB
     */
    int getFluidVolume(double fluidHeight);

    /**
     * @return the total fluid capacity in mB for this node
     */
    default int getFluidCapacity() {
        return this.getFluidVolume(this.getMaxFluidHeight());
    }

    /**
     * Checks if this node can connect to another component.
     * This method is called reciprocally, and if either returns true, the connection can be formed
     *
     * @param other the other node
     * @return true if this can connect to the other
     */
    boolean canConnect(IAgriIrrigationComponent other);

    /**
     * @return a collection of all positions to which this node can connect to
     */
    Collection<BlockPos> getPotentialNeighbours();

    /**
     * Used for rendering fluid transients, fluid will "flow" from sources to sinks
     *
     * @return true if this component acts as a source
     */
    boolean isSource();

    /**
     * Used for rendering fluid transients, fluid will "flow" from sources to sinks
     *
     * @return true if this component acts as a sink
     */
    boolean isSink();

    CompoundNBT serializeData();

    void deserializeData(CompoundNBT tag);
}
