package com.infinityraider.agricraft.api.v1.irrigation;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

/**
 * Interface representing connections in an AgriCraft irrigation network
 *
 * This interface is for interaction only and should not be implemented
 */
public interface IAgriIrrigationConnection {
    /**
     * @return the node from which this connections goes
     */
    IAgriIrrigationNode from();

    /**
     * @return the node to which this connections goes
     */
    IAgriIrrigationNode to();

    /**
     * @return the in-world position from which this connection goes
     */
    BlockPos fromPos();

    /**
     * @return the in-world position to which this connection goes
     */
    default BlockPos toPos() {
        return this.fromPos().offset(this.direction());
    }

    /**
     * @return The directionality of this connection
     */
    Direction direction();
}
