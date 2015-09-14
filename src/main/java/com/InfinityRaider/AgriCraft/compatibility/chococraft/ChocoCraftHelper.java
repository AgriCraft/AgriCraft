package com.InfinityRaider.AgriCraft.compatibility.chococraft;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;

public class ChocoCraftHelper extends ModHelper {
    @Override
    protected void initPlants() {
        try {
            CropPlantHandler.registerPlant(new CropPlantGhyshal());
        } catch(Exception e) {
            if(ConfigurationHandler.debug) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String modId() {
        return Names.Mods.chococraft;
    }
}
