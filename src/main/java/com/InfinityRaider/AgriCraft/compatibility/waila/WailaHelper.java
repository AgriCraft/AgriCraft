package com.InfinityRaider.AgriCraft.compatibility.waila;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import cpw.mods.fml.common.event.FMLInterModComms;

public class WailaHelper extends ModHelper {
    @Override
    protected void onInit() {
        FMLInterModComms.sendMessage(Names.Mods.waila, "register", "com.InfinityRaider.AgriCraft.compatibility.waila.WailaRegistry.initWaila");
    }

    @Override
    protected String modId() {
        return Names.Mods.waila;
    }
}
