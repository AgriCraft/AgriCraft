package com.InfinityRaider.AgriCraft.compatibility.bloodmagic;

import WayofTime.alchemicalWizardry.api.harvest.HarvestRegistry;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;

public class BloodMagicHelper extends ModHelper {
    @Override
    protected void init() {
        HarvestRegistry.registerHarvestHandler(new BloodMagicHarvesthandler());
    }

    @Override
    protected void initPlants() {}

    @Override
    protected String modId() {
        return Names.Mods.bloodMagic;
    }
}
