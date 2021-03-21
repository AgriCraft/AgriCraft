package com.infinityraider.agricraft.api.v1.irrigation;

import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.Collection;

/**
 * Interface representing nodes in an AgriCraft irrigation network
 */
public interface IAgriIrrigationNode {
    /**
     * @return a Collection of all Components which have this as node
     */
    Collection<IAgriIrrigationComponent> getComponents();

    /**
     * @return the minimum fluid level, relative to the world coordinates (corresponds to an empty capacity)
     */
    double getMinFluidHeight();

    /**
     * @return the maximum fluid level, relative to the world coordinates (corresponds to a full capacity)
     */
    double getMaxFluidHeight();

    /**
     * @return the total fluid capacity in mB for this node
     */
    int getFluidCapacity() ;

    /**
     * Fetches the volume of fluid for a given fluid height, relative to the world coordinates
     * @param fluidHeight the fluid height
     * @return the volume of fluid in mB
     */
    default int getFluidVolume(double fluidHeight) {
        double f = (fluidHeight - this.getMinFluidHeight())/(this.getMaxFluidHeight() - this.getMinFluidHeight());
        return (int) MathHelper.lerp(f, 0, this.getFluidCapacity());
    }

    /**
     * Fetches the height of the fluid, relative to the world coordinates as a function of the content in the tank
     * @param content the volume content of fluid in the node
     * @return the height of the fluid level in world coordinates
     */
    default double getFluidHeight(int content) {
        if(content <= 0) {
            return this.getMinFluidHeight();
        }
        if(content >= this.getFluidCapacity()) {
            return this.getMaxFluidHeight();
        }
        double f = (content + 0.0D)/this.getFluidCapacity();
        return MathHelper.lerp(f, this.getMinFluidHeight(), this.getMaxFluidHeight());
    }

    /**
     * Checks if this node can connect to another component.
     * This method is called reciprocally, and is thus directional.
     *
     * @param other the other node
     * @return true if this can connect to the other
     */
    boolean canConnect(IAgriIrrigationNode other, Direction from);

    /**
     * @return a collection of all positions to which this node can connect to
     */
    Collection<Tuple<Direction, BlockPos>> getPotentialConnections();

    /**
     * Used for rendering fluid transients, fluid will "flow" from sources to sinks
     *
     * @return true if this component is currently acting as a source
     */
    boolean isSource();

    /**
     * Used for rendering fluid transients, fluid will "flow" from sources to sinks
     *
     * @return true if this component is currently acting as a sink
     */
    boolean isSink();
}
