/*
 */
package com.infinityraider.agricraft.api.v1.fertilizer;

import net.minecraft.item.ItemStack;

/**
 * An interface for managing fertilizers supported by AgriCraft.
 *
 * @author The AgriCraft Team.
 */
public interface IFertilizerRegistry {

	/**
	 * Checks if the given item is any form of supported fertilizer (e.g. bone
	 * meal).
	 *
	 * <p>
	 * Note: A "fertilizer" is any item that can be applied to a growing plant,
	 * regardless of the effect.
	 * </p>
	 *
	 * @param fertilizer Any item.
	 * @return True if AgriCraft knows how to handle the given item as
	 * fertilizer.
	 */
	boolean isFertilizer(ItemStack fertilizer);
	
	IAgriFertilizer getFertilizer(ItemStack fertilizer);
	
	/**
	 * Maps an ItemStack to a fertilizer instance.
	 * 
	 * @param stack the item stack to use as a fertilizer.
	 * @param fertilizer the IAgriFertilizer instance to associate with the item stack.
	 * @return 
	 */
	boolean registerFertilizer(ItemStack stack, IAgriFertilizer fertilizer);
	
	/**
	 * Unregisters a fertilizer from the registry.
	 * 
	 * @param stack the stack to stop using as a fertilizer.
	 * @return if the fertilizer was successfully unregistered.
	 */
	boolean unregisterFertilizer(ItemStack stack);

}
