package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlantCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nonnull;

public class JsonPlantCallBackBushy extends JsonPlantCallback {
    public static final String ID = AgriCraft.instance.getModId() + ":" + "bushy";
    private static final JsonPlantCallback INSTANCE = new JsonPlantCallBackBushy();

    public static JsonPlantCallback getInstance() {
        return INSTANCE;
    }

    private JsonPlantCallBackBushy() {
        super(ID);
    }

    public void onEntityCollision(@Nonnull IAgriCrop crop, Entity entity) {
        if (entity instanceof LivingEntity) {
            entity.setMotionMultiplier(crop.asTile().getBlockState(), new Vector3d(0.8F, 0.75D, 0.8F));
        }
    }
}
