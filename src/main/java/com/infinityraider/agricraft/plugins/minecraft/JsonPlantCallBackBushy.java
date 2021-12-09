package com.infinityraider.agricraft.plugins.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;

public class JsonPlantCallBackBushy implements IJsonPlantCallback {
    public static final String ID = AgriCraft.instance.getModId() + ":" + "bushy";

    private static final IJsonPlantCallback INSTANCE = new JsonPlantCallBackBushy();

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

    private JsonPlantCallBackBushy() {}

    @Override
    public void onEntityCollision(@Nonnull IAgriCrop crop, Entity entity) {
        if (entity instanceof LivingEntity) {
            entity.setMotionMultiplier(crop.asTile().getBlockState(), new Vector3d(0.8F, 0.75D, 0.8F));
        }
    }
}
