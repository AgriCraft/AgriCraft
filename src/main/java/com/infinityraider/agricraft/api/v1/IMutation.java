package com.InfinityRaider.AgriCraft.api.v1;

import net.minecraft.item.ItemStack;

/** This interface is for you to read data about a mutation and shouldn't be used to be implemented in your classes */
public interface IMutation {
    /** returns the result */
    public ItemStack getResult();

    /** should always return an array of size 2 */
    public ItemStack[] getParents();

    public double getChance();

    public void setChance(double d);
}
