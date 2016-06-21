/*
 */
package com.infinityraider.agricraft.api.v1.seed;

import net.minecraft.item.ItemStack;

/**
 *
 * @author RlonRyan
 */
public interface ISeedRegistry {
	
	boolean isHandled(ItemStack stack);
	
	boolean addSeedHandler(ItemStack stack, ISeedHandler handler);
	
	boolean removeSeedHandler(ItemStack stack);
	
	ISeedHandler getSeedHandler(ItemStack stack);
	
	default boolean isSeed(ItemStack stack) {
		ISeedHandler handler = getSeedHandler(stack);
		return handler != null && handler.isValid(stack);
	}
	
	default AgriSeed getSeed(ItemStack stack) {
		ISeedHandler handler = getSeedHandler(stack);
		return handler == null ? null : handler.getSeed(stack);
	}
	
}
