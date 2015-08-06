package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import com.InfinityRaider.AgriCraft.tileentity.peripheral.TileEntityPeripheral;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MethodAnalyze implements IMethod {
    @Override
    public String getName() {
        return "analyze";
    }

    @Override
    public Object[] call(TileEntityPeripheral peripheral, World world, int x, int y, int z, ItemStack journal, Object... args) throws MethodException {
        peripheral.startAnalyzing();
        return new Object[0];
    }
}
