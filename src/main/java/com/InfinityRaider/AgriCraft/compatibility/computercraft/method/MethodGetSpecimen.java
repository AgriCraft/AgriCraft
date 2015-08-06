package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import com.InfinityRaider.AgriCraft.container.ContainerSeedAnalyzer;
import com.InfinityRaider.AgriCraft.tileentity.peripheral.TileEntityPeripheral;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MethodGetSpecimen implements IMethod {
    @Override
    public String getName() {
        return "getSeedInAnalyzer";
    }

    @Override
    public Object[] call(TileEntityPeripheral peripheral, World world, int x, int y, int z, ItemStack journal, Object... args) throws MethodException {
        ItemStack stack = peripheral.getStackInSlot(ContainerSeedAnalyzer.seedSlotId);
        return new Object[] {stack==null?null:stack.getDisplayName()};
    }
}
