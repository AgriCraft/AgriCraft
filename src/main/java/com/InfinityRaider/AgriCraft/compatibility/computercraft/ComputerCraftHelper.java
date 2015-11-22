package com.InfinityRaider.AgriCraft.compatibility.computercraft;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.block.Block;

public class ComputerCraftHelper extends ModHelper {
    public static void registerPeripheralProvider(IPeripheralProvider provider) {
        dan200.computercraft.api.ComputerCraftAPI.registerPeripheralProvider(provider);
    }

    public static Block getComputerBlock() {
        return (Block) Block.blockRegistry.getObject("ComputerCraft:CC-Computer");
    }

    @Override
    protected String modId() {
        return Names.Mods.computerCraft;
    }
}
