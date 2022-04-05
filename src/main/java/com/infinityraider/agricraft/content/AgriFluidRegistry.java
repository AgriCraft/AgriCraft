package com.infinityraider.agricraft.content;

import com.infinityraider.agricraft.api.v1.content.IAgriContent;
import com.infinityraider.agricraft.content.irrigation.FluidTankWater;
public class AgriFluidRegistry {
    public static final IAgriContent.Fluids ACCESSOR = new Accessor();

    // tank water
    public static final FluidTankWater TANK_WATER = new FluidTankWater();

    private static final class Accessor implements IAgriContent.Fluids {
        private Accessor() {
        }

        @Override
        public FluidTankWater getTankWater() {
            return TANK_WATER;
        }
    }
}
