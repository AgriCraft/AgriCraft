package com.infinityraider.agricraft.api.v1.misc;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;

/**
 * Interface for harvestable objects.
 */
public interface IAgriHarvestable {

    /**
     * Determines if the object can currently be harvested or not.
     *
     * @return if the object may be harvested.
     */
    boolean canBeHarvested();

    /**
     * Harvests the object.
     *
     * @param consumer a consumer that accepts the items that were harvested.
     * @param player the player which harvests the crop, may be null if it is harvested by
     * automation.
     * @return if the harvest was successful.
     */
    @Nonnull
    ActionResultType harvest(@Nonnull Consumer<ItemStack> consumer, @Nullable PlayerEntity player);

}
