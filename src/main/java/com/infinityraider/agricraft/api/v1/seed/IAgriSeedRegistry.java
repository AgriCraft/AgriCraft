/*
 */
package com.infinityraider.agricraft.api.v1.seed;

import net.minecraft.item.ItemStack;

/**
 *
 * @author RlonRyan
 */
public interface IAgriSeedRegistry {
	
	boolean isHandled(ItemStack stack);
	
	boolean addSeedHandler(ItemStack stack, IAgriSeedHandler handler);
	
	boolean removeSeedHandler(ItemStack stack);
	
	IAgriSeedHandler getSeedHandler(ItemStack stack);
	
	default boolean isSeed(ItemStack stack) {
		IAgriSeedHandler handler = getSeedHandler(stack);
		return handler != null && handler.isValid(stack);
	}
	
	default AgriSeed getSeed(ItemStack stack) {
		IAgriSeedHandler handler = getSeedHandler(stack);
		return handler == null ? null : handler.getSeed(stack);
	}
	
}
