/*
 */
package com.infinityraider.agricraft.api.misc;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Interface representing a possible plant product.
 */
public interface IAgriHarvestProduct {

    public static final String PRODUCT_MARKER_TAG = "agri_product";
    public static final String PRODUCT_MIN_TAG = "agri_product_min";
    public static final String PRODUCT_MAX_TAG = "agri_product_max";
    public static final String PRODUCT_CHANCE_TAG = "agri_product_chance";

    boolean isHidden();

    @Nonnull
    Item getItem();

    @Nonnull
    NBTTagCompound getTag();

    int getMeta();

    int getMinAmount();

    int getMaxAmount();

    int getAmount(Random rand);

    double getChance();

    @Nonnull
    ItemStack toStack();

    @Nonnull
    ItemStack toStack(Random rand);

    @Nonnull
    ItemStack toLabeledStack();

}
