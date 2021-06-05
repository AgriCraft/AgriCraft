package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlantCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

import javax.annotation.Nonnull;

public class JsonPlantCallBackPoisoning extends JsonPlantCallback {
    public static final String ID = AgriCraft.instance.getModId() + ":" + "poisoning";
    private static final JsonPlantCallback INSTANCE = new JsonPlantCallBackPoisoning();

    public static JsonPlantCallback getInstance() {
        return INSTANCE;
    }

    private JsonPlantCallBackPoisoning() {
        super(ID);
    }

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
