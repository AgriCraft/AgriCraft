/*
 */
package com.infinityraider.agricraft.api.v1.misc;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Interface for harvestable objects.
 *
 * @author RlonRyan
 */
public interface IHarvestable {
	
	/**
	 * Determines if the object can currently be harvested or not.
	 * 
	 * @return if the object may be harvested.
	 */
	boolean canHarvest();
	
	/**
	 * Harvests the object.
	 *
	 * @param player the player which harvests the crop, may be null if it is
	 * harvested by automation.
	 * @return if the harvest was successful.
	 */
	boolean harvest(@Nullable EntityPlayer player);
	
	/**
	 * Retrieves a list of possible fruits from harvesting of this object.
	 * 
	 * @return a list of possible fruits.
	 */
	@Nonnull
	List<ItemStack> getFruits();
	
}
