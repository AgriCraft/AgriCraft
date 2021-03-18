package com.infinityraider.agricraft.impl.v1.irrigation;

import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationConnection;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import net.minecraft.util.Direction;

public class IrrigationNetworkConnection implements IAgriIrrigationConnection {
    private final IAgriIrrigationNode from;
    private final IAgriIrrigationNode to;
    private final Direction direction;

    public IrrigationNetworkConnection(IAgriIrrigationNode from, IAgriIrrigationNode to, Direction direction) {
        this.from = from;
        this.to = to;
        this.direction = direction;
    }

    @Override
    public IAgriIrrigationNode from() {
        return this.from;
    }

    @Override
    public IAgriIrrigationNode to() {
        return this.to;
    }

    @Override
    public Direction direction() {
        return this.direction;
    }
}
