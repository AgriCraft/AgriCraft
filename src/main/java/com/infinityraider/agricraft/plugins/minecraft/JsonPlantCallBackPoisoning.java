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

public class JsonPlantCallBackPoisoning implements IJsonPlantCallback {
    public static final String ID = AgriCraft.instance.getModId() + ":" + "poisoning";

    private static final IJsonPlantCallback INSTANCE = new JsonPlantCallBackPoisoning();

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

    private JsonPlantCallBackPoisoning() {}

    public void onEntityCollision(@Nonnull IAgriCrop crop, Entity entity) {
        if (entity instanceof LivingEntity && !entity.isDiscrete() && !entity.getLevel().isClientSide()) {
            LivingEntity livingEntity = ((LivingEntity) entity);
            if (!livingEntity.hasEffect(MobEffects.POISON)) {
                MobEffectInstance poison = new MobEffectInstance(MobEffects.POISON, (int) (20 * crop.getStats().getAverage()));
                ((LivingEntity) entity).addEffect(poison);
            }
        }
    }
}
