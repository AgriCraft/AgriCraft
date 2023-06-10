package com.agricraft.agricraft.api.crop;

import com.agricraft.agricraft.api.content.items.IAgriRakeItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Interface for rakeable objects.
 */
public interface IAgriRakeable {

	/**
	 * Determines if the object can currently be raked or not.
	 *
	 * @return if the object may be harvested.
	 */
	boolean canBeRaked(@NotNull IAgriRakeItem item, @NotNull ItemStack stack, @Nullable LivingEntity entity);

	/**
	 * Rakes the object.
	 *
	 * @param consumer a consumer that accepts all the products of raking this rakeable.
	 * @param entity   the entity which is performing the raking, may be null if raked through automation
	 * @return if the harvest was successful.
	 */
	boolean rake(@NotNull Consumer<ItemStack> consumer, @Nullable LivingEntity entity);

}
