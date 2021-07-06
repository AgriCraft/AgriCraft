package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlantCallback;
import net.minecraft.entity.Entity;

import javax.annotation.Nonnull;

public class JsonPlantCallBackBurn extends JsonPlantCallback {
    public static final String ID = AgriCraft.instance.getModId() + ":" + "burn";
    private static final JsonPlantCallback INSTANCE = new JsonPlantCallBackBurn();

    public static JsonPlantCallback getInstance() {
        return INSTANCE;
    }

    private JsonPlantCallBackBurn() {
        super(ID);
    }

    public void onEntityCollision(@Nonnull IAgriCrop crop, Entity entity) {
        entity.setFire((int) crop.getStats().getAverage());
    }
}
