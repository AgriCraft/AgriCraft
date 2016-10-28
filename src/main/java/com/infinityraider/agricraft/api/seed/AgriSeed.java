/*
 */
package com.infinityraider.agricraft.api.seed;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.stat.IAgriStat;

/**
 * A simple class for representing seeds. Seeds are immutable objects, for
 * safety reasons.
 *
 *
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
