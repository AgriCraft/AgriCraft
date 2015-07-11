package com.InfinityRaider.AgriCraft.api.v1;

import net.minecraft.item.ItemStack;

/** Make sure object.equals(Object object) is implemented correctly if you decide to create your own Mutation class */
public interface IMutation {
    /** returns the result */
    public ItemStack getResult();

    /** should always return an array of size 2 */
    public ItemStack[] getParents();

    public double getChance();

    public void setChance(double d);
}
