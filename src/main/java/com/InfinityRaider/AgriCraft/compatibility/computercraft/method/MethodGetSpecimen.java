package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.InfinityRaider.AgriCraft.container.ContainerSeedAnalyzer;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityPeripheral;

public class MethodGetSpecimen extends MethodBase {
    @Override
    public String getName() {
        return "getSeedInAnalyzer";
    }

    @Override
    public Object[] call(TileEntityPeripheral peripheral, World world, int x, int y, int z, ItemStack journal, Object... args) throws MethodException {
        ItemStack stack = peripheral.getStackInSlot(ContainerSeedAnalyzer.seedSlotId);
        return new Object[] {stack==null?null:stack.getDisplayName()};
    }

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        return new ArrayList<MethodParameter>();
    }
}
