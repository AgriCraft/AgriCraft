package com.infinityraider.agricraft.plugins.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nonnull;

public class JsonPlantCallBackBurn implements IJsonPlantCallback {
    public static final String ID = AgriCraft.instance.getModId() + ":" + "burn";

    private static final IJsonPlantCallback INSTANCE = new JsonPlantCallBackBurn();

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

    private JsonPlantCallBackBurn() {}

    @Override
    public void onEntityCollision(@Nonnull IAgriCrop crop, Entity entity) {
        entity.setSecondsOnFire((int) crop.getStats().getAverage());
    }
}
