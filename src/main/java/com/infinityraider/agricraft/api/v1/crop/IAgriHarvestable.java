package com.infinityraider.agricraft.api.v1.crop;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;

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
    ActionResultType harvest(@Nonnull Consumer<ItemStack> consumer, @Nullable LivingEntity entity);

}
