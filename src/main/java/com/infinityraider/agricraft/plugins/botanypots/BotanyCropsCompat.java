package com.infinityraider.agricraft.plugins.botanypots;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;

public class BotanyCropsCompat {
    public static void registerCapability() {
        AgriCraft.instance.proxy().registerCapability(CapabilityBotanyPotAgriCrop.getInstance());
    }

    public static IInfRecipeSerializer getAgriCropInfoSerializer() {
        return AgriCropInfo.SERIALIZER;
    }
}
