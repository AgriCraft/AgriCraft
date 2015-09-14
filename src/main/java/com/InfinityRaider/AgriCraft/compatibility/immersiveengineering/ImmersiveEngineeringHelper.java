package com.InfinityRaider.AgriCraft.compatibility.immersiveengineering;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;

public class ImmersiveEngineeringHelper extends ModHelper {
    @Override
    protected void initPlants() {
        try {
            CropPlantHandler.registerPlant(new CropPlantHemp());
        } catch (Exception e) {
            if(ConfigurationHandler.debug) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String modId() {
        return "ImmersiveEngineering";
    }
}
