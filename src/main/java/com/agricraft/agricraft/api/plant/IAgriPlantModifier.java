package com.agricraft.agricraft.api.plant;

import com.agricraft.agricraft.api.crop.AgriCrop;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Interface to modify the behaviours of a plant planted in a crop.
 */
public interface IAgriPlantModifier {

	/**
	 * Returns the light level emitted when this callback is present
	 *
	 * @param crop the crop on which a plant with this modifier is present
	 * @return the light level emitted by the crop
	 */
	default int getBrightness(AgriCrop crop) {
		return 0;
	}

	/**
	 * Returns the redstone power emitted when this callback is present
	 *
	 * @param crop the crop on which a plant with this modifier is present
	 * @return the redstone power emitted by the crop
	 */
	default int getRedstonePower(AgriCrop crop) {
		return 0;
	}

	/**
	 * Modifier for custom actions right after this plant has been planted.
	 *
	 * @param crop   the crop on which this plant was planted
	 * @param entity the entity who planted the plant (can be null in case it wasn't planted by an entity)
	 */
	default void onPlanted(AgriCrop crop, LivingEntity entity) {

	}

	/**
	 * Modifier for custom actions right after this plant has been spawned on crop sticks due to a mutation or spread.
	 *
	 * @param crop the crop on which this plant spawned
	 */
	default void onSpawned(AgriCrop crop) {
	}

	/**
	 * Modifier for custom actions right after receiving a random tick
	 *
	 * @param crop   the crop receiving the random tick
	 * @param random the random source
	 */
	default void onRandomTick(AgriCrop crop, RandomSource random) {
	}

	/**
	 * Modifier for custom actions right after a successful growth increment.
	 * Growth ticks are called after random tick.
	 *
	 * @param crop the crop on which this plant is planted
	 */
	default void onGrowth(AgriCrop crop) {
	}

	/**
	 * Modifier for custom actions right after a plant is removed (for instance by being killed by weeds).
	 *
	 * @param crop the crop on which this plant was planted
	 */
	default void onRemoved(AgriCrop crop) {
	}

	/**
	 * Modifier for custom actions right after a successful harvest of this plant.
	 *
	 * @param crop   the crop on which this plant is planted
	 * @param entity the entity who harvested the plant (can be null in case it wasn't planted by an entity)
	 */
	default void onHarvest(AgriCrop crop, @Nullable LivingEntity entity) {
	}

	/**
	 * Modifier for custom actions right after a successful clipping of this plant.
	 *
	 * @param crop    the crop on which this plant is planted
	 * @param clipper the ItemStack holding the clipper item
	 * @param entity  the entity who clipped the plant (can be null in case it wasn't clipped by an entity)
	 */
	default void onClipped(AgriCrop crop, ItemStack clipper, @Nullable LivingEntity entity) {
	}

	/**
	 * Modifier for custom actions right after the plant is fertilized
	 *
	 * @param crop       the crop fertilized
	 * @param fertilizer the fertilizer
	 * @param random     the random source
	 */
	default void onFertilized(AgriCrop crop, ItemStack fertilizer, RandomSource random) {
	}

	/**
	 * Modifier for custom actions right after crop sticks holding this plant have been broken.
	 *
	 * @param crop   the crop on which this plant was planted
	 * @param entity the entity who broke the crop sticks (can be null in case it wasn't broken by an entity)
	 */
	default void onBroken(AgriCrop crop, @Nullable LivingEntity entity) {
	}

	/**
	 * Modifier for custom actions when an entity collides with a crop where this plant is planted.
	 *
	 * @param crop   the crop on which this plant was planted
	 * @param entity the entity which collided
	 */
	default void onEntityCollision(AgriCrop crop, Entity entity) {
	}

	/**
	 * Modifier for custom actions before a crop is right-clicked.
	 * Runs before default right click behaviour.
	 *
	 * @param crop   the crop on which this plant was planted
	 * @param stack  the stack with which was right-clicked on the crop
	 * @param entity the entity which used the item, can be null if usage happens through automation
	 * @return an empty optional to allow continuation of default right click behaviour, an optional containing an action result to pass to the right click chain, prevents default behaviour
	 */
	default Optional<ItemInteractionResult> onRightClickPre(AgriCrop crop, ItemStack stack, @Nullable Entity entity) {
		return Optional.empty();
	}

	/**
	 * Modifier for custom actions after a crop is right-clicked.
	 * Runs after default right click behaviour.
	 *
	 * @param crop   the crop on which this plant was planted
	 * @param stack  the stack with which was right-clicked on the crop
	 * @param entity the entity which used the item, can be null if usage happens through automation
	 * @return an optional containing an action result to pass to the right click chain, or empty to continue the default chain
	 */
	default Optional<ItemInteractionResult> onRightClickPost(AgriCrop crop, ItemStack stack, @Nullable Entity entity) {
		return Optional.empty();
	}

}
