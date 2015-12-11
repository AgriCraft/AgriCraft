package com.InfinityRaider.AgriCraft.compatibility.gardenstuff;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.block.Block;

public class GardenStuffHelper extends ModHelper {
    @Override
    protected void onPostInit() {
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

