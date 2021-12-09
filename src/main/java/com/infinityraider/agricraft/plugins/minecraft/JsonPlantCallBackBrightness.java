package com.infinityraider.agricraft.plugins.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;

import javax.annotation.Nonnull;

public class JsonPlantCallBackBrightness implements IJsonPlantCallback {
    public static final String ID = AgriCraft.instance.getModId() + ":" + "brightness";

    private static final IJsonPlantCallback INSTANCE = new JsonPlantCallBackBrightness();

    private static final IJsonPlantCallback.Factory FACTORY = new IJsonPlantCallback.Factory() {
        @Override
        public String getId() {
            return ID;
        }

        @Override
        public IJsonPlantCallback makeCallBack(JsonElement json) throws JsonParseException {
            return INSTANCE;
        }
    };

    public static IJsonPlantCallback.Factory getFactory() {
        return FACTORY;
    }

    private JsonPlantCallBackBrightness() {}

    @Override
    public int getBrightness(@Nonnull IAgriCrop crop) {
        return (int) (16*crop.getGrowthStage().growthPercentage());
    }
}
