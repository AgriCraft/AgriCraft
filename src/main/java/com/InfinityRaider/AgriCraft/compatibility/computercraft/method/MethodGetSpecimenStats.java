package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import com.InfinityRaider.AgriCraft.api.v1.ISeedStats;
import com.InfinityRaider.AgriCraft.apiimpl.v1.PlantStats;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityPeripheral;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MethodGetSpecimenStats implements IMethod {
    @Override
    public String getName() {
        return "getSpecimenStats";
    }

    @Override
    public Object[] call(TileEntityPeripheral peripheral, World world, int x, int y, int z, ItemStack journal, Object... args) throws MethodException {
        if(!peripheral.isSpecimenAnalyzed()) {
            return null;
        }
        ISeedStats stats = PlantStats.getStatsFromStack(peripheral.getSpecimen());
        if( stats==null ) {
            return null;
        }
        return new Object[] {stats.getGrowth(), stats.getGain(), stats.getStrength()};
    }
}
