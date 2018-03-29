/*
 */
package com.infinityraider.agricraft.api.v1.seed;

import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * A simple class for representing seeds. Seeds are immutable objects, for safety reasons.
 *
 *
 */
public final class AgriSeed {

    @Nonnull
    private final IAgriPlant plant;
    @Nonnull
    private final IAgriStat stat;

    public AgriSeed(@Nonnull IAgriPlant plant, @Nonnull IAgriStat stat) {
        this.plant = Preconditions.checkNotNull(plant, "The plant in an AgriSeed may not be null!");
        this.stat = Preconditions.checkNotNull(stat, "The stat in an AgriSeed may not be null!");
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

    @Nonnull
    public ItemStack toStack() {
        // Delegate.
        return toStack(1);
    }

    @Nonnull
    public ItemStack toStack(int size) {
        // Get the stack.
        final ItemStack stack = Preconditions.checkNotNull(this.plant.getSeed());

        // Get the tag.
        final NBTTagCompound tag = Optional.ofNullable(stack.getTagCompound())
                .map(NBTTagCompound::copy)
                .orElseGet(NBTTagCompound::new);

        // Write the stat to the tag.
        this.stat.writeToNBT(tag);

        // Return a new stack.
        // Thanks @darthvader45
        ItemStack ret = new ItemStack(stack.getItem(), size, stack.getMetadata());
        ret.setTagCompound(tag);
        return ret;
    }

    @Override
    public final boolean equals(Object obj) {
        return (obj instanceof AgriSeed)
                && (this.equals((AgriSeed) obj));
    }

    public final boolean equals(AgriSeed other) {
        return (other != null)
                && (this.plant.equals(other.plant))
                && (this.stat.equals(other.stat));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.plant);
        hash = 71 * hash + Objects.hashCode(this.stat);
        return hash;
    }

}
