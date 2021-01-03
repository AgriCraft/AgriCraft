package com.infinityraider.agricraft.api.v1.misc;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.infinityraider.agricraft.api.v1.items.IAgriRakeItem;
import net.minecraft.entity.LivingEntity;
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
    boolean canBeRaked(@Nonnull IAgriRakeItem item, @Nonnull ItemStack stack, @Nullable LivingEntity entity);

    /**
     * Rakes the object.
     *
     * @param consumer a consumer that accepts all the products of raking this rakeable.
     * @param entity the entity which is performing the raking, may be null if raked through automation
     * @return if the harvest was successful.
     */
    boolean rake(@Nonnull Consumer<ItemStack> consumer, @Nullable LivingEntity entity);

}
