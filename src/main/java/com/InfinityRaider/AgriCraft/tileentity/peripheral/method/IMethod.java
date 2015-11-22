package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.tileentity.peripheral.TileEntityPeripheral;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IMethod {
    String getName();

    Object[] call(TileEntityPeripheral peripheral, World world, int x, int y, int z, ItemStack journal, Object... args) throws MethodException;

    String getDescription();

    String signature();
}
