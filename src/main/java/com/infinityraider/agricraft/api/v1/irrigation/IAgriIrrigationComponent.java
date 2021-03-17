package com.infinityraider.agricraft.api.v1.irrigation;

import net.minecraft.tileentity.TileEntity;

public interface IAgriIrrigationComponent {
    IAgriIrrigationNode getNode();

    default TileEntity castToTile() {
        return (TileEntity) this;
    }
}
