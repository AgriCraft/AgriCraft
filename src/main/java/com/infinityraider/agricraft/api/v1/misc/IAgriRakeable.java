/*
 */
package com.infinityraider.agricraft.api.v1.misc;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Interface for rakeable objects.
 */
public interface IAgriRakeable {

    /**
     * Determines if the object can currently be raked or not.
     *
     * @return if the object may be harvested.
     */
    boolean canBeRaked();

    /**
     * Rakes the object.
     *
     * @param consumer a consumer that accepts all the products of raking this rakeable.
     * @param player the player which harvests the crop, may be null if it is harvested by
     * automation.
     * @return if the harvest was successful.
     */
    boolean onRaked(@Nonnull Consumer<ItemStack> consumer, @Nullable EntityPlayer player);

}
