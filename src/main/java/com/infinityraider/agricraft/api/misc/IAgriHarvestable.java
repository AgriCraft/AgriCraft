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
	 * @param player the player which harvests the crop, may be null if it is
	 * harvested by automation.
	 * @return if the harvest was successful.
	 */
	boolean onHarvest(@Nullable EntityPlayer player);

}
