package com.InfinityRaider.AgriCraft.compatibility.minetweaker;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import minetweaker.MineTweakerAPI;

public class MinetweakerHelper extends ModHelper {
    @Override
    protected void onInit() {
        MineTweakerAPI.registerClass(CustomWood.class);
        MineTweakerAPI.registerClass(SeedMutation.class);
        MineTweakerAPI.registerClass(SeedBlacklist.class);
        MineTweakerAPI.registerClass(SpreadChance.class);
        MineTweakerAPI.registerClass(CropProduct.class);
        MineTweakerAPI.registerClass(Growing.class);
        MineTweakerAPI.registerClass(Growing.FertileSoils.class);
        MineTweakerAPI.registerClass(Growing.Soil.class);
        MineTweakerAPI.registerClass(Growing.Brightness.class);
        MineTweakerAPI.registerClass(Growing.BaseBlock.class);
        MineTweakerAPI.registerClass(WeedRaking.class);
    }

    @Override
    protected String modId() {
        return Names.Mods.minetweaker;
    }
}
