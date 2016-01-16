package com.InfinityRaider.AgriCraft.compatibility.rotarycraft;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.utility.LogHelper;

public class RotaryCraftHelper extends ModHelper {
    @Override
    protected void initPlants() {
        try {
            //CropPlantHandler.registerPlant(new CropPlantCanola());
        } catch (Exception e) {
            LogHelper.printStackTrace(e);
        }
    }

    @Override
    protected String modId() {
        return "RotaryCraft";
    }
}
