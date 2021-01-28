package com.infinityraider.agricraft.impl.v1.plant;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlantCallback {
    private static final Map<String, PlantCallback> callbacks = Maps.newConcurrentMap();

    public static Optional<PlantCallback> get(String id) {
        return Optional.ofNullable(callbacks.get(id));
    }

    public static List<PlantCallback> get(List<String> ids) {
        return ids.stream().map(PlantCallback::get).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    private final String id;

    protected PlantCallback(String id) {
        Preconditions.checkArgument(!callbacks.containsKey(id), "Can not create two callbacks with identical ids:" + id);
        this.id = id;
        callbacks.put(id, this);
    }

    public final String getId() {
        return this.id;
    }

    public void onPlanted(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {}

    public void onSpawned(@Nonnull IAgriCrop crop) {}

    public void onGrowth(@Nonnull IAgriCrop crop) {}

    public void onRemoved(@Nonnull IAgriCrop crop) {}

    public void onHarvest(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {}

    public void onBroken(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {}

    public void onEntityCollision(@Nonnull IAgriCrop crop, Entity entity) {}
}
