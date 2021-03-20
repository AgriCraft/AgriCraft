package com.infinityraider.agricraft.api.v1.irrigation;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public interface IAgriIrrigationConnection {
    IAgriIrrigationNode from();

    IAgriIrrigationNode to();

    BlockPos fromPos();

    default BlockPos toPos() {
        return this.fromPos().offset(this.direction());
    }

    Direction direction();
}
