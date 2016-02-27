package com.InfinityRaider.AgriCraft.compatibility.hydraulicraft;

import com.InfinityRaider.AgriCraft.reference.Names;
import cpw.mods.fml.common.Loader;

public class HydraulicraftAPIWrapper {
    private static HydraulicraftAPIWrapper instance;

    public static HydraulicraftAPIWrapper getInstance() {
        if(instance == null) {
            instance = Loader.isModLoaded(Names.Mods.hydraulicraft) ? new HydraulicraftAPI() : new HydraulicraftAPIWrapper();
        }
        return instance;
    }

    protected  HydraulicraftAPIWrapper() {}

    protected void registerTrolley() {}
}
