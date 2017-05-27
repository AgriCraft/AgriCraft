/*
 */
package com.infinityraider.agricraft.apiimpl;

import com.infinityraider.agricraft.api.misc.IAgriHarvestProduct;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author Ryan
 */
public final class AgriHarvestProduct implements IAgriHarvestProduct {
    
    @Nonnull
    private final Item item;
    @Nonnull
    private final NBTTagCompound tag;
    private final int meta;
    private final int minAmount;
    private final int maxAmount;
    private final double chance;
    private final boolean hidden;

    public AgriHarvestProduct(@Nonnull Item item, @Nullable NBTTagCompound tag, int meta, int minAmount, int maxAmount, double chance, boolean hidden) {
        this.item = Objects.requireNonNull(item);
        this.tag = (tag == null) ? new NBTTagCompound() : tag;
        this.meta = meta;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.chance = chance;
        this.hidden = hidden;
    }

    @Override
    public Item getItem() {
        return item;
    }

    @Override
    public int getMeta() {
        return meta;
    }

    @Override
    public NBTTagCompound getTag() {
        return tag.copy();
    }

    @Override
    public int getMinAmount() {
        return minAmount;
    }

    @Override
    public int getMaxAmount() {
        return maxAmount;
    }

    @Override
    public int getAmount(Random rand) {
        return this.getMinAmount() + rand.nextInt(this.maxAmount - this.minAmount);
    }

    @Override
    public double getChance() {
        return chance;
    }
    
    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public ItemStack toStack() {
        final ItemStack stack = new ItemStack(item, 1, meta);
        final NBTTagCompound data = tag.copy();
        stack.setTagCompound(data);
        return stack;
    }

    @Override
    public ItemStack toStack(Random rand) {
        final ItemStack stack = new ItemStack(item, getAmount(rand), meta);
        final NBTTagCompound data = tag.copy();
        stack.setTagCompound(data);
        return stack;
    }

    @Override
    public ItemStack toLabeledStack() {
        final ItemStack stack = new ItemStack(item, 1, meta);
        final NBTTagCompound data = tag.copy();
        data.setBoolean(PRODUCT_MARKER_TAG, true);
        data.setInteger(PRODUCT_MIN_TAG, minAmount);
        data.setInteger(PRODUCT_MAX_TAG, maxAmount);
        data.setDouble(PRODUCT_CHANCE_TAG, chance);
        stack.setTagCompound(data);
        return stack;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.item);
        hash = 97 * hash + Objects.hashCode(this.tag);
        hash = 97 * hash + this.meta;
        hash = 97 * hash + this.minAmount;
        hash = 97 * hash + this.maxAmount;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.chance) ^ (Double.doubleToLongBits(this.chance) >>> 32));
        hash = 97 * hash + (this.hidden ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof AgriHarvestProduct) {
            final AgriHarvestProduct other = (AgriHarvestProduct) obj;
            return (this.hidden == other.hidden)
                    && (this.minAmount == other.minAmount)
                    && (this.maxAmount == other.maxAmount)
                    && (this.chance == other.chance)
                    && (this.meta == other.meta)
                    && (Objects.equals(this.item, other.item))
                    && (Objects.equals(this.tag, other.tag));
        } else {
            return false;
        }
    }
    
    
    
}
