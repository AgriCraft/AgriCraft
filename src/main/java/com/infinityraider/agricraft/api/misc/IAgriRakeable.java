/*
 */
package com.infinityraider.agricraft.api.misc;

import java.util.Random;
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
	 * @param player the player which harvests the crop, may be null if it is
	 * harvested by automation.
	 * @return if the harvest was successful.
	 */
	boolean onRaked(@Nullable EntityPlayer player);

	/**
	 * Calls the given consumer with the assortment of random products and the
	 * seed item gained from raking this object. For example, to get a list of
	 * the random products, pass the consumer list::add.
	 *
	 * @param consumer the consumer to collect the random products with.
	 * @param random the random to select the random products with.
	 */
	void getRakeProducts(@Nonnull Consumer<ItemStack> consumer, @Nonnull Random random);

	/**
	 * Calls the given consumer with the assortment of all possible products
	 * gained from raking this object. For example, to get a list of the random
	 * fruits, pass the consumer list::add.
	 *
	 * @param consumer the consumer to add the products to.
	 */
	void getAllRakeProducts(@Nonnull Consumer<ItemStack> consumer);

}
