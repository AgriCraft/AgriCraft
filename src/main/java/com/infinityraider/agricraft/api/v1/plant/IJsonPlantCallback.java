package com.infinityraider.agricraft.api.v1.plant;

import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface representing default callback behaviours which can be used by AgriCraft json plants.
 * The json plants simply forward the calls to these instances.
 * Implementations of this interface must be registered through the api in order to be recognized by the json parser
 *
 * Existing instances can be retrieved using AgriApi.getJsonPlantCallback()
 * New instances can be registered using AgriApi.registerJsonPlantCallback() *
 */
public interface IJsonPlantCallback {
    /**
     * The id used to refer to callback implementations in the json files, must be unique
     * @return a unique string representing this callback behaviour
     */
    String getId();

    /**
     * Returns the redstone power emitted when this callback is present
     * @param crop the crop on which a plant with this callback is present
     * @return the redstone power emitted by the crop
     */
    default int getRedstonePower(@Nonnull IAgriCrop crop) {
        return 0;
    }

    /**
     * Callback for custom actions right after this plant has been planted on crop sticks,
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this plant was planted
     * @param entity the entity who planted the plant (can be null in case it wasn't planted by an entity)
     */
    default void onPlanted(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {}

    /**
     * Callback for custom actions right after this plant has been spawned on crop sticks due to a mutation or spread,
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this plant spawned
     */
    default void onSpawned(@Nonnull IAgriCrop crop) {}

    /**
     * Callback for custom actions right after a successful growth increment,
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this plant is planted
     */
    default void onGrowth(@Nonnull IAgriCrop crop) {}

    /**
     * Callback for custom actions right after a plant is removed (for instance by being killed by weeds),
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this plant was planted
     */
    default void onRemoved(@Nonnull IAgriCrop crop) {}

    /**
     * Callback for custom actions right after a successful harvest of this plant,
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this plant is planted
     * @param entity the entity who harvested the plant (can be null in case it wasn't planted by an entity)
     */
    default void onHarvest(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {}

    /**
     * Callback for custom actions right after a successful clipping of this plant,
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this plant is planted
     * @param clipper the ItemStack holding the clipper item
     * @param entity the entity who clipped the plant (can be null in case it wasn't clipped by an entity)
     */
    default void onClipped(@Nonnull IAgriCrop crop, @Nonnull ItemStack clipper, @Nullable LivingEntity entity) {}

    /**
     * Callback for custom actions right after crop sticks holding this plant have been broken,
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this plant was planted
     * @param entity the entity who broke the crop sticks (can be null in case it wasn't broken by an entity)
     */
    default void onBroken(@Nonnull IAgriCrop crop, @Nullable LivingEntity entity) {}

    /**
     * Callback for custom actions when an entity collides with a crop where this plant is planted
     * does nothing by default, but can be overridden for special behaviours
     * @param crop the crop on which this plant was planted
     * @param entity the entity which collided
     */
    default void onEntityCollision(@Nonnull IAgriCrop crop, Entity entity) {}
}
