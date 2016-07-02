/*
 */
package com.infinityraider.agricraft.api.v1.seed;

import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

/**
 * A simple class for representing seeds. Seeds are immutable objects, for
 * safety reasons.
 *
 * @author RlonRyan
 */
public class AgriSeed {

	@Nonnull
	private final IAgriPlant plant;
	@Nonnull
	private final IAgriStat stat;

	public AgriSeed(@Nonnull IAgriPlant plant, @Nonnull IAgriStat stat) {
		this.plant = plant;
		this.stat = stat;
	}

	@Nonnull
	public IAgriPlant getPlant() {
		return this.plant;
	}

	@Nonnull
	public IAgriStat getStat() {
		return this.stat;
	}
	
	@Nonnull
	public AgriSeed withPlant(@Nonnull IAgriPlant plant) {
		return new AgriSeed(plant, stat);
	}
	
	@Nonnull
	public AgriSeed withStat(@Nonnull IAgriStat stat) {
		return new AgriSeed(plant, stat);
	}

	public ItemStack toStack() {
		ItemStack stack = this.plant.getSeed();
		this.stat.writeToNBT(stack.getTagCompound());
		return stack;
	}

}
