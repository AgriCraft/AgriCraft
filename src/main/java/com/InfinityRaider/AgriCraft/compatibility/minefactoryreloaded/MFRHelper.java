package com.InfinityRaider.AgriCraft.compatibility.minefactoryreloaded;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import powercrystals.minefactoryreloaded.api.FactoryRegistry;

public class MFRHelper extends ModHelper {
    @Override
    protected void init() {
        FactoryRegistry.sendMessage("registerHarvestable", new AgriCraftHarvestable());
    }

    @Override
    protected void initPlants() {

    }

    @Override
    protected String modId() {
        return Names.Mods.mfr;
    }
}
