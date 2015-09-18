package com.InfinityRaider.AgriCraft.compatibility.gardenstuff;

import net.minecraft.block.Block;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.reference.Names;

public class GardenStuffHelper extends ModHelper {
    @Override
    protected void postTasks() {
        registerSoils();
    }

    private void registerSoils() {
        GrowthRequirementHandler.addDefaultSoil(new BlockWithMeta((Block) Block.blockRegistry.getObject("GardenCore:garden_farmland"), 0));
    }

    @Override
    protected String modId() {
        return Names.Mods.gardenStuff;
    }
}

