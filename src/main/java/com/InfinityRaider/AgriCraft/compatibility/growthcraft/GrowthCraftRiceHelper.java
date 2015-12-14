package com.InfinityRaider.AgriCraft.compatibility.growthcraft;

import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.exception.DuplicateCropPlantException;

public class GrowthCraftRiceHelper extends ModHelper {
    @Override
    protected void initPlants() {
        try {
            CropPlant ricePlant = new CropPlantGrowthCraftRice();
            CropPlantHandler.registerPlant(ricePlant);
        } catch (DuplicateCropPlantException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String modId() {
        return Names.Mods.growthcraft+"|Rice";
    }
}
