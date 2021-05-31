package com.infinityraider.agricraft.plugins.botania;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.reference.Names;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.api.BotaniaAPI;

public class BotaniaCompat {
    public static void registerHarvestables() {
        BotaniaAPI.instance().registerHornHarvestableBlock(
                new ResourceLocation(AgriCraft.instance.getModId(), Names.Blocks.CROP_STICKS),
                AgriHornHarvestable.INSTANCE
        );
        BotaniaAPI.instance().registerHornHarvestableBlock(
                new ResourceLocation(AgriCraft.instance.getModId(), Names.Blocks.CROP_PLANT),
                AgriHornHarvestable.INSTANCE
        );
    }
}
