package com.infinityraider.agricraft.plugins.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

import javax.annotation.Nonnull;

public class JsonPlantCallBackThorns implements IJsonPlantCallback {
    public static final String ID = AgriCraft.instance.getModId() + ":" + "thorns";

    private static final IJsonPlantCallback INSTANCE = new JsonPlantCallBackThorns();

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

    private JsonPlantCallBackThorns() {}

    public void onEntityCollision(@Nonnull IAgriCrop crop, Entity entity) {
        double damage = crop.getGrowthStage().growthPercentage() * crop.getStats().getAverage();
        entity.attackEntityFrom(DamageSource.CACTUS, (float) damage);
    }
}
