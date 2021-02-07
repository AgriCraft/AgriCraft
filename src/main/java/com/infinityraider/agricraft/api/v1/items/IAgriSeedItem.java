package com.infinityraider.agricraft.api.v1.items;

import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneCarrierItem;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import net.minecraft.item.ItemStack;

import java.util.Optional;

/**
 * Implemented in the AgriCraft seed Item object
 * To obtain, check if an ItemStack's item is an instance of this class and cast.
 */
public interface IAgriSeedItem extends IAgriGeneCarrierItem {
}
