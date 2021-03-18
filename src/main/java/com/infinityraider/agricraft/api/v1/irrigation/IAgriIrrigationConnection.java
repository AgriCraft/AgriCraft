package com.infinityraider.agricraft.api.v1.irrigation;

import net.minecraft.util.Direction;

public interface IAgriIrrigationConnection {
    IAgriIrrigationNode from();

    IAgriIrrigationNode to();

    Direction direction();
}
