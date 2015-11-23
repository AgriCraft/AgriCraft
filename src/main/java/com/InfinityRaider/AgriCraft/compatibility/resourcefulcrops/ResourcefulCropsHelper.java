package com.InfinityRaider.AgriCraft.compatibility.resourcefulcrops;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;

public class ResourcefulCropsHelper extends ModHelper {
    @Override
    protected String modId() {
        return Names.Mods.resourcefulCrops;
    }

    @Override
    protected  void initPlants() {
        ResourcefulCropsAPIwrapper.getInstance().init();
    }
}
