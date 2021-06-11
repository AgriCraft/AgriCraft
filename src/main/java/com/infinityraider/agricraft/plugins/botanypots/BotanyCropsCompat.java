package com.infinityraider.agricraft.plugins.botanypots;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.CropCapability;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;

public class BotanyCropsCompat {
    public static void registerCapability() {
        CropCapability.registerInstance(BotanyPotAgriCropInstance.getInstance());
    }

    public static void registerEventHandler() {
        AgriCraft.instance.registerEventHandler(BotanyPotsHandler.getInstance());
    }

    public static IInfRecipeSerializer getAgriCropInfoSerializer() {
        return AgriCropInfo.SERIALIZER;
    }
}
