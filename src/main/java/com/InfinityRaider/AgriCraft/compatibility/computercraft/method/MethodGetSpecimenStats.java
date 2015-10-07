package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import com.InfinityRaider.AgriCraft.apiimpl.v1.PlantStats;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityPeripheral;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;

public class MethodGetSpecimenStats extends MethodBase {
    @Override
    public String getName() {
        return "getSpecimenStats";
    }

    @Override
    public Object[] call(TileEntityPeripheral peripheral, World world, int x, int y, int z, ItemStack journal, Object... args) throws MethodException {
        if(!peripheral.isSpecimenAnalyzed()) {
            return null;
        }
        PlantStats stats = new PlantStats(peripheral.getSpecimen().stackTagCompound);
        if( stats==null ) {
            return null;
        }
        return new Object[] {stats.growth, stats.gain, stats.strength};
    }

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        return new ArrayList<MethodParameter>();
    }
}
