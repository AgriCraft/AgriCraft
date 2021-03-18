package com.infinityraider.agricraft.api.v1.irrigation;

import com.infinityraider.agricraft.api.v1.AgriApi;
import net.minecraft.tileentity.TileEntity;

public interface IAgriIrrigationComponent {
    IAgriIrrigationNode getNode();

    default TileEntity castToTile() {
        return (TileEntity) this;
    }

    default IAgriIrrigationNetwork getNetwork() {
        return AgriApi.getIrrigationNetwork(this);
    }
}
