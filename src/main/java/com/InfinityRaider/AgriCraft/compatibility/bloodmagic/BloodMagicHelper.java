package com.InfinityRaider.AgriCraft.compatibility.bloodmagic;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;

import java.lang.reflect.Method;

public class BloodMagicHelper extends ModHelper {
    @Override
    @SuppressWarnings("unchecked")
    protected void onInit() {
        try {
            Class harvestRegistry = Class.forName("WayofTime.alchemicalWizardry.api.harvest.HarvestRegistry");
            Class harvestHandler = Class.forName("WayofTime.alchemicalWizardry.api.harvest.IHarvestHandler");
            Method registerHarvestHandler = harvestRegistry.getMethod("registerHarvestHandler", harvestHandler);
            registerHarvestHandler.invoke(null, new HarvestHandler());
        } catch(Exception e) {
            if(ConfigurationHandler.debug) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String modId() {
        return Names.Mods.bloodMagic;
    }
}
