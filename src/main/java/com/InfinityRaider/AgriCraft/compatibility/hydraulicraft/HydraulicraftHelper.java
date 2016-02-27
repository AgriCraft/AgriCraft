package com.InfinityRaider.AgriCraft.compatibility.hydraulicraft;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;

public class HydraulicraftHelper extends ModHelper {
    @Override
    protected void onPostInit() {
        HydraulicraftAPIWrapper.getInstance().registerTrolley();
    }

    @Override
    protected String modId() {
        return Names.Mods.hydraulicraft;
    }
}
