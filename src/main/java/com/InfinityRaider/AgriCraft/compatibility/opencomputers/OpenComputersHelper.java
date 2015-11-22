package com.InfinityRaider.AgriCraft.compatibility.opencomputers;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.block.Block;

public class OpenComputersHelper extends ModHelper {
    @Override
    protected String modId() {
        return Names.Mods.openComputers;
    }

    public static Block getComputerBlock() {
        return (Block) Block.blockRegistry.getObject("");
    }
}
