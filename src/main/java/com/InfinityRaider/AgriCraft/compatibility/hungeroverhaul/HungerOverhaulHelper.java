package com.InfinityRaider.AgriCraft.compatibility.hungeroverhaul;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import cpw.mods.fml.common.event.FMLInterModComms;

public final class HungerOverhaulHelper extends ModHelper {
    @Override
    protected void onInit() {
        FMLInterModComms.sendMessage("HungerOverhaul", "BlacklistRightClick", "com.InfinityRaider.AgriCraft.blocks.BlockCrop");
    }

    @Override
    protected String modId() {
        return Names.Mods.hungerOverhaul;
    }
}
