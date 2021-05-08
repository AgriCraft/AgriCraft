package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlantCallback;

import javax.annotation.Nonnull;

public class JsonPlantCallBackRedstone extends JsonPlantCallback {
    public static final String ID = AgriCraft.instance.getModId() + ":" + "redstone";
    private static final JsonPlantCallback INSTANCE = new JsonPlantCallBackRedstone();

    public static JsonPlantCallback getInstance() {
        return INSTANCE;
    }

    private JsonPlantCallBackRedstone() {
        super(ID);
    }

    @Override
    public int getRedstonePower(@Nonnull IAgriCrop crop) {
        return (int) (crop.getGrowthStage().growthPercentage()*15);
    }
}
