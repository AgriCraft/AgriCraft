package com.InfinityRaider.AgriCraft.compatibility.gardenstuff;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.exception.DuplicateCropPlantException;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class GardenStuffHelper extends ModHelper {
    @Override
    protected void initPlants() {
        Item seed = (Item) Item.itemRegistry.getObject("GardenTrees:candelilla_seeds");
        Item fruit = (Item) Item.itemRegistry.getObject("GardenTrees:candelilla");
        Block plant = (Block) Block.blockRegistry.getObject("GardenTrees:candelilla_bush");
        if(seed != null && fruit != null && plant != null) {
            try {
                CropPlantHandler.registerPlant(new CropPlantGardenStuff(seed, plant, fruit));
            } catch (DuplicateCropPlantException e) {
                LogHelper.printStackTrace(e);
            }
        }
    }

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

