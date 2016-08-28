/*
 */
package com.infinityraider.agricraft.compat.vanilla;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.adapter.IAgriAdapter;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.apiimpl.PlantRegistry;
import com.infinityraider.agricraft.farming.PlantStats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 *
 * @author RlonRyan
 */
public class SeedWrapper implements IAgriAdapter<AgriSeed> {

	@Override
	public boolean accepts(Object obj) {
		return (obj instanceof ItemStack) && resolve((ItemStack)obj) != null;
	}

	@Override
	public AgriSeed getValue(Object obj) {
		return (obj instanceof ItemStack) ? resolve((ItemStack)obj) : null;
	}

	private AgriSeed resolve(ItemStack stack) {
		int[] ids = OreDictionary.getOreIDs(stack);
		for (int i : ids) {
			String id = OreDictionary.getOreName(i);
			IAgriPlant plant = PlantRegistry.getInstance().getPlant(id.replace("_seed", "_plant"));
			if (plant != null) {
				AgriCore.getLogger("AgriCraft").debug("Resolved OreDict Seed: \"{0}\"!", id);
				return new AgriSeed(plant, new PlantStats());
			}
		}
		return null;
	}

}
