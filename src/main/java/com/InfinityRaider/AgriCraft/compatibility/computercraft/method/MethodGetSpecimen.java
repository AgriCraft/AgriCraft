package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import com.InfinityRaider.AgriCraft.container.ContainerSeedAnalyzer;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityPeripheral;
import net.minecraft.item.ItemStack;

public class MethodGetSpecimen extends MethodBasePeripheral {
    public MethodGetSpecimen() {
        super("getSpecimen");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityPeripheral peripheral) throws MethodException {
        ItemStack stack = peripheral.getStackInSlot(ContainerSeedAnalyzer.seedSlotId);
        return new Object[] {stack==null?null:stack.getDisplayName()};
    }
}
