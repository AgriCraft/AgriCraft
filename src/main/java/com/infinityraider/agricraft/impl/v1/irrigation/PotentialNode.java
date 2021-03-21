package com.infinityraider.agricraft.impl.v1.irrigation;

import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

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

    public Optional<IAgriIrrigationNode> getNode() {
        return this.getComponent().getNode(this.getDirection().getOpposite());
    }

    public BlockPos getToPos() {
        return this.getComponent().getTile().getPos();
    }

    public BlockPos getFromPos() {
        return this.getToPos().offset(this.getDirection().getOpposite());
    }

    public Direction getDirection() {
        return this.direction;
    }
}
