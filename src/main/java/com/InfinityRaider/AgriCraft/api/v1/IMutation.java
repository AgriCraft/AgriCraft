package com.InfinityRaider.AgriCraft.api.v1;

import net.minecraft.item.ItemStack;

/** This interface is for you to read data about a mutation and shouldn't be used to be implemented in your classes */
public interface IMutation {
    /** returns the result */
    ItemStack getResult();

    /** should always return an array of size 2 */
    ItemStack[] getParents();

    /**
     * The chance a mutation will actually be applied if it has been triggered.
     * When a cross crop receives a growth tick, there is a percentage chance nothing will happen.
     * Then there is a percentage chance weeds will spawn (if enabled in the config)
     * Then either a mutation happens or a spread happens based on the chance defined in the config.
     *
     * If a mutation is chosen and this is te resulting mutation, this is the chance the mutation will actually be applied.
     *
     * @return the mutation chance
     */
    double getChance();

    /**
     * Sets the mutation chance for this mutation
     * @param d the chance, should be in the interval [0, 1](0: mutation never passes, 1: mutation always passes)
     */
    void setChance(double d);
}
