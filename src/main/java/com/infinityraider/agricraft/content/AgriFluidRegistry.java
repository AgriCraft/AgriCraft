package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.api.v1.content.IAgriContent;
import com.infinityraider.agricraft.content.irrigation.FluidTankWater;
import com.infinityraider.infinitylib.utility.registration.ModContentRegistry;
import com.infinityraider.infinitylib.utility.registration.RegistryInitializer;

public class AgriFluidRegistry extends ModContentRegistry implements IAgriContent.Fluids {
    private static final AgriFluidRegistry INSTANCE = new AgriFluidRegistry();

    public static AgriFluidRegistry getInstance() {
        return INSTANCE;
    }

    // tank water
    public final RegistryInitializer<FluidTankWater> tank_water;

    private AgriFluidRegistry() {
        super();
        this.tank_water = this.fluid(FluidTankWater::new);
    }

    @Override
    public FluidTankWater getTankWater() {
        return this.tank_water.get();
    }
}