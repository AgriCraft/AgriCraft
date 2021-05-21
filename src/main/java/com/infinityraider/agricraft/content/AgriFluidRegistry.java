package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.content.irrigation.FluidTankWater;

public class AgriFluidRegistry {
    private static final AgriFluidRegistry INSTANCE = new AgriFluidRegistry();

    public static AgriFluidRegistry getInstance() {
        return INSTANCE;
    }

    // crop plant
    public final FluidTankWater tank_water;

    private AgriFluidRegistry() {
        this.tank_water = new FluidTankWater();
    }
}
