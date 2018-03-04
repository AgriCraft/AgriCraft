package com.infinityraider.agricraft.compat.computer.methods;

import com.infinityraider.agricraft.tiles.TileEntityPeripheral;
import com.infinityraider.agricraft.container.ContainerSeedAnalyzer;
import net.minecraft.item.ItemStack;

public class MethodGetSpecimen extends MethodBasePeripheral {

    public MethodGetSpecimen() {
        super("getSpecimen");
    }

    @Override
    protected Object[] onMethodCalled(TileEntityPeripheral peripheral) throws InvocationException {
        ItemStack stack = peripheral.getStackInSlot(ContainerSeedAnalyzer.SEED_SLOT_ID);
        return new Object[]{stack == null ? null : stack.getDisplayName()};
    }
}
