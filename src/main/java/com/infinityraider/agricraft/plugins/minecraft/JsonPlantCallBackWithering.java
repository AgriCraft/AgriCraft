package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlantCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import javax.annotation.Nonnull;

public class JsonPlantCallBackWithering extends JsonPlantCallback {
    public static final String ID = AgriCraft.instance.getModId() + ":" + "withering";
    private static final JsonPlantCallback INSTANCE = new JsonPlantCallBackWithering();

    public static JsonPlantCallback getInstance() {
        return INSTANCE;
    }

    private JsonPlantCallBackWithering() {
        super(ID);
    }

    public void onEntityCollision(@Nonnull IAgriCrop crop, Entity entity) {
        if(entity instanceof LivingEntity) {
            EffectInstance wither = new EffectInstance(Effects.WITHER, (int) (10*crop.getStats().getAverage()));
            ((LivingEntity) entity).addPotionEffect(wither);
        }
    }
}
