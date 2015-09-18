package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityPeripheral;

public class MethodHasJournal extends MethodBase {
    @Override
    public String getName() {
        return "hasJournal";
    }

    @Override
    public Object[] call(TileEntityPeripheral peripheral, World world, int x, int y, int z, ItemStack journal, Object... args) throws MethodException {
        return new Object[] {journal!=null};
    }

    @Override
    protected ArrayList<MethodParameter> getParameters() {
        return new ArrayList<MethodParameter>();
    }
}
