package com.InfinityRaider.AgriCraft.compatibility.minefactoryreloaded;

import powercrystals.minefactoryreloaded.api.FactoryRegistry;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;

public class MFRHelper extends ModHelper {
    @Override
    protected void init() {
        FactoryRegistry.sendMessage("registerHarvestable", new AgriCraftHarvestable());
    }

    @Override
    protected String modId() {
        return Names.Mods.mfr;
    }
}
