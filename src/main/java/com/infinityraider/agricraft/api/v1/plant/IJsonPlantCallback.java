package com.infinityraider.agricraft.api.v1.plant;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.requirement.IAgriGrowthRequirement;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

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
     * Finds a registered json plant callback factory from their id
     *
     * @param id the id
     * @return optional containing the callback, or empty if no such callback is registered
     */
    static Optional<IJsonPlantCallback.Factory> getCallback(String id) {
        return AgriApi.getJsonPlantCallback(id);
    }

    /**
     * Tries to register a json plant callback behaviour
     *
     * @param callback the callback factory to register
     * @return true if successful (will fail in case a callback with the same id is already registered)
     */
    static boolean registerCallback(IJsonPlantCallback.Factory callback) {
        return AgriApi.registerJsonPlantCallback(callback);
    }

    /**
     * Returns the light level emitted when this callback is present
     * @param crop the crop on which a plant with this callback is present
     * @return the light level emitted by the crop
     */
    default int getBrightness(@Nonnull IAgriCrop crop) {
        return 0;
    }

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

    /**
     * Callback for custom actions before a crop is right clicked,
     * does nothing by default, but can be overridden for special behaviours.
     * Runs before default right click behaviour.
     *
     * @param crop the crop on which this plant was planted
     * @param stack the stack with which was right clicked on the crop
     * @param entity the entity which used the item, can be null if usage happens through automation
     * @return an empty optional to allow continuation of default right click behaviour,
     * an optional containing an action result to pass to the right click chain, prevents default behaviour
     */
    default Optional<InteractionResult> onRightClickPre(@Nonnull IAgriCrop crop, @Nonnull ItemStack stack, @Nullable Entity entity) {
        return Optional.empty();
    }

    /**
     * Callback for custom actions after a crop is right clicked,
     * does nothing by default, but can be overridden for special behaviours.
     * Runs after default right click behaviour.
     *
     * @param crop the crop on which this plant was planted
     * @param stack the stack with which was right clicked on the crop
     * @param entity the entity which used the item, can be null if usage happens through automation
     * @return an optional containing an action result to pass to the right click chain, or empty to continue the default chain
     */
    default Optional<InteractionResult> onRightClickPost(@Nonnull IAgriCrop crop, @Nonnull ItemStack stack, @Nullable Entity entity) {
        return Optional.empty();
    }

    /**
     * Called right before the growth requirement for a plant with this callback is initialized
     * @param builder the growth requirement builder
     */
    default void onGrowthReqInitialization(IAgriGrowthRequirement.Builder builder) {}

    /**
     * Factory class to parse IJsonPlantCallback objects from json elements
     */
    interface Factory {
        /**
         * @return a unique ID for this callback factory
         */
        String getId();

        /**
         * Builds a callback based on the passed in json element
         * @param json the json element to parse from
         * @return a callback behaviour
         * @throws JsonParseException in case an invalid json element was passed in
         */
        IJsonPlantCallback makeCallBack(JsonElement json) throws JsonParseException;

        /**
         * Utility method to register the factory
         * @return the factory itself, or null if registration has failed
         */
        @Nullable
        default Factory register() {
            if(IJsonPlantCallback.registerCallback(this)) {
                return this;
            }
            return null;
        }
    }
}
