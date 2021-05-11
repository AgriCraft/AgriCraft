package com.infinityraider.agricraft.plugins.botanypots;

import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;

public class BotanyCropsCompat {
    public static IInfRecipeSerializer getAgriCropInfoSerializer() {
        return AgriCropInfo.SERIALIZER;
    }
}
