package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.impl.v1.plant.PlantCallback;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

import javax.annotation.Nonnull;

public class PlantCallBackThorns extends PlantCallback {
    public static final String ID = AgriCraft.instance.getModId() + ":" + "thorns";
    private static final PlantCallback INSTANCE = new PlantCallBackThorns();

    public static PlantCallback getInstance() {
        return INSTANCE;
    }

    private PlantCallBackThorns() {
        super(ID);
    }

    public void onEntityCollision(@Nonnull IAgriCrop crop, Entity entity) {
        double damage = crop.getGrowthStage().growthPercentage() * crop.getStats().getAverage();
        entity.attackEntityFrom(DamageSource.CACTUS, (float) damage);
    }
}
