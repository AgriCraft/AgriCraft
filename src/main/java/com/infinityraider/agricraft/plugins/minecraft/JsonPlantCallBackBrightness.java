package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlantCallback;

import javax.annotation.Nonnull;

public class JsonPlantCallBackBrightness extends JsonPlantCallback {
    public static final String ID = AgriCraft.instance.getModId() + ":" + "brightness";
    private static final JsonPlantCallback INSTANCE = new JsonPlantCallBackBrightness();

    public static JsonPlantCallback getInstance() {
        return INSTANCE;
    }

    private JsonPlantCallBackBrightness() {
        super(ID);
    }

    @Override
    public int getBrightness(@Nonnull IAgriCrop crop) {
        return (int) (16*crop.getGrowthStage().growthPercentage());
    }
}
