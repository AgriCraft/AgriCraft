package com.InfinityRaider.AgriCraft.compatibility.opencomputers;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import li.cil.oc.api.Driver;
import net.minecraft.block.Block;

public class OpenComputersHelper extends ModHelper {
    @Override
    protected String modId() {
        return Names.Mods.openComputers;
    }

    protected void onPostInit() {
        Driver.add(new AgriCraftEnvironment());
    }

    public static Block getComputerBlock() {
        return (Block) Block.blockRegistry.getObject("OpenComputers:case1");
    }
}