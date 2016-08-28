/*
 */
package com.infinityraider.agricraft.apiimpl;

import com.infinityraider.agricraft.api.adapter.IAgriAdapterRegistry;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.vanilla.SeedWrapper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 *
 * @author RlonRyan
 */
public class SeedRegistry {
	
	private static final IAgriAdapterRegistry<AgriSeed> INSTANCE = new AdapterRegistry<>();
	
	static {
		OreDictionary.registerOre("wheat_seed", new ItemStack(Items.WHEAT_SEEDS));
		OreDictionary.registerOre("potato_seed", new ItemStack(Items.POTATO));
		//OreDictionary.registerOre("carrot_seed", new ItemStack(Items.CARROT));
		//OreDictionary.registerOre("pumpkin_seed", new ItemStack(Items.PUMPKIN_SEEDS));
		//OreDictionary.registerOre("melon_seed", new ItemStack(Items.MELON_SEEDS));
		//OreDictionary.registerOre("sugarcane_seed", new ItemStack(Items.REEDS));
		INSTANCE.registerAdapter(new SeedWrapper());
	}
	
	public static IAgriAdapterRegistry<AgriSeed> getInstance() {
		return INSTANCE;
	}

}
