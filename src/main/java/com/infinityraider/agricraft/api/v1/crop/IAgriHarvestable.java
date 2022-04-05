package com.infinityraider.agricraft.api.v1.crop;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for harvestable objects.
 */
public interface IAgriHarvestable {

    /**
     * Determines if the object can currently be harvested or not.
     *
     * @param entity the entity wishing to harvest
     * @return if the object may be harvested.
     */
    boolean canBeHarvested(@Nullable LivingEntity entity);

    /**
     * Harvests the object.
     *
     * @param consumer a consumer that accepts the items that were harvested.
     * @param entity the entity which harvests the crop, may be null if it is harvested by
     * automation.
     * @return if the harvest was successful.
     */
    @Nonnull
    InteractionResult harvest(@Nonnull Consumer<ItemStack> consumer, @Nullable LivingEntity entity);

}
