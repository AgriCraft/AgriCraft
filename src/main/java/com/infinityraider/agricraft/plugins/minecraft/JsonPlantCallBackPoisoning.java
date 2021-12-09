package com.infinityraider.agricraft.plugins.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

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
        if (entity instanceof LivingEntity && !entity.isSneaking() && !entity.getEntityWorld().isRemote) {
            LivingEntity livingEntity = ((LivingEntity) entity);
            if (!livingEntity.isPotionActive(Effects.POISON)) {
                EffectInstance poison = new EffectInstance(Effects.POISON, (int) (20 * crop.getStats().getAverage()));
                ((LivingEntity) entity).addPotionEffect(poison);
            }
        }
    }
}
