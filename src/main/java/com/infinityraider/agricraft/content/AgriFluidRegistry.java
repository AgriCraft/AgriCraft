package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.api.v1.content.IAgriContent;
import com.infinityraider.agricraft.content.irrigation.FluidTankWater;
import net.minecraft.fluid.Fluid;

public class AgriFluidRegistry implements IAgriContent.Fluids {
    private static final AgriFluidRegistry INSTANCE = new AgriFluidRegistry();

    public static AgriFluidRegistry getInstance() {
        return INSTANCE;
    }

    // crop plant
    public final FluidTankWater tank_water;

    private AgriFluidRegistry() {
        this.tank_water = new FluidTankWater();
    }

    @Override
    public Fluid getTankWater() {
        return this.tank_water;
    }
}
