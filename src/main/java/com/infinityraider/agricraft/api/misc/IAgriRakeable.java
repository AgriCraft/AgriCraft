/*
 */
package com.infinityraider.agricraft.api.misc;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;

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

}
