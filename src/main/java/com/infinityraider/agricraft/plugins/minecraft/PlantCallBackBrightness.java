package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.impl.v1.plant.PlantCallback;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlantCallBackBrightness extends PlantCallback {
    public static final String ID = AgriCraft.instance.getModId() + ":" + "brightness";
    private static final PlantCallback INSTANCE = new PlantCallBackBrightness();

    public static PlantCallback getInstance() {
        return INSTANCE;
    }

    private PlantCallBackBrightness() {
        super(ID);
    }

    //TODO: Implement brightness

    @Override
    public void onPlanted(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {}

    @Override
    public void onSpawned(@Nonnull IAgriCrop crop) {}

    @Override
    public void onGrowth(@Nonnull IAgriCrop crop) {}

    @Override
    public void onRemoved(@Nonnull IAgriCrop crop) {}

    @Override
    public void onHarvest(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {}

    @Override
    public void onBroken(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {}
}
