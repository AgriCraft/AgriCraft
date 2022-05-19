package com.infinityraider.agricraft.plugins.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;

public class JsonPlantCallBackWithering implements IJsonPlantCallback {
    public static final String ID = AgriCraft.instance.getModId() + ":" + "withering";

    private static final IJsonPlantCallback INSTANCE = new JsonPlantCallBackWithering();

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

    private JsonPlantCallBackWithering() {}

    public void onEntityCollision(@Nonnull IAgriCrop crop, Entity entity) {
        if(entity instanceof LivingEntity) {
            MobEffectInstance wither = new MobEffectInstance(MobEffects.WITHER, (int) (10*crop.getStats().getAverage()));
            ((LivingEntity) entity).addEffect(wither);
        }
    }
}
