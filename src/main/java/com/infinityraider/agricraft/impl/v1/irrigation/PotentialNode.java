package com.infinityraider.agricraft.impl.v1.irrigation;

import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class PotentialNode {
    private final IAgriIrrigationComponent component;
    private final Direction direction;

    public PotentialNode(IAgriIrrigationComponent component, Direction direction) {
        this.component = component;
        this.direction = direction;
    }

    public IAgriIrrigationComponent getComponent() {
        return this.component;
    }

    public IAgriIrrigationNode getNode() {
        return this.getComponent().getNode();
    }

    public BlockPos getPos() {
        return this.getComponent().castToTile().getPos();
    }

    public Direction getDirection() {
        return this.direction;
    }
}
