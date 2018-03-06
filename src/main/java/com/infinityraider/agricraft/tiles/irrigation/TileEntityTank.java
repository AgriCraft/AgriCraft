package com.infinityraider.agricraft.tiles.irrigation;

// Imports would go here, if there were any.

import com.infinityraider.agricraft.api.v1.misc.IAgriFluidComponent;
import com.infinityraider.infinitylib.utility.WorldHelper;
import net.minecraft.util.EnumFacing;


public class TileEntityTank extends TileEntityChannel {
    
    public static final int TANK_FLUID_CAPACITY = 16_000;
    public static final int TANK_FLUID_HEIGHT_MIN = 0_000_000;
    public static final int TANK_FLUID_HEIGHT_MAX = 1_000_000;
    public static final int TANK_FLUID_SYNC_THRESHOLD = 250;

    // Just a basic delegating constructor.
    public TileEntityTank() {
        this(TANK_FLUID_CAPACITY, TANK_FLUID_HEIGHT_MIN, TANK_FLUID_HEIGHT_MAX, TANK_FLUID_SYNC_THRESHOLD);
    }

    public TileEntityTank(int fluidCapacity, int fluidHeightMin, int fluidHeightMax, int fluidSyncThreshold) {
        super(fluidCapacity, fluidHeightMin, fluidHeightMax, fluidSyncThreshold);
    }

    @Override
    protected byte classifyConnection(EnumFacing side) {
        final IAgriFluidComponent component = WorldHelper.getTile(world, pos.offset(side), IAgriFluidComponent.class).orElse(null);
        if (component == null) {
            return 0;
        } else if (component instanceof TileEntityTank) {
            return 2;
        } else {
            return 1;
        }
    }

}
