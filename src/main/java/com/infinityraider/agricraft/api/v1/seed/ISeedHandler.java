/*
 */
package com.infinityraider.agricraft.api.v1.seed;

import net.minecraft.item.ItemStack;

/**
 *
 * @author RlonRyan
 */
public interface ISeedHandler {
	
	boolean isValid(ItemStack stack);
	
	AgriSeed getSeed(ItemStack stack);
	
}
