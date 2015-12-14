package com.InfinityRaider.AgriCraft.farming.mutation;

import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Represents the result of a specific <code>INewSeedStrategy</code> containing
 * the seed plus meta and the chance to happen.
 */
public class CrossOverResult {

    private final Item seed;
    private final int meta;
    private final double chance;

    private int growth;
    private int gain;
    private int strength;

    public CrossOverResult(Item seed, int meta, double chance) {
        this.seed = seed;
        this.meta = meta;
        this.chance = chance;
    }

    /** Creates a new instance based on the planted seed of the given TE. Does not validate the TE */
    public static CrossOverResult fromTileEntityCrop(TileEntityCrop crop) {
        Item seed = crop.getSeedStack().getItem();
        int meta = crop.getSeedStack().getItemDamage();
        double chance = ((double) crop.getPlant().getSpreadChance())/100.0;

        return new CrossOverResult(seed, meta, chance);
    }

    /** Creates a new instanced based off the result of the given mutation. Does not validate the mutation object */
    public static CrossOverResult fromMutation(Mutation mutation) {
        Item seed = mutation.getResult().getItem();
        int meta = mutation.getResult().getItemDamage();

        return new CrossOverResult(seed, meta, mutation.getChance());
    }

    public ItemStack toStack() {
        ItemStack stack = new ItemStack(seed, 1, meta);
        NBTTagCompound tag = new NBTTagCompound();
        CropPlantHandler.setSeedNBT(tag, (short) growth, (short) gain, (short) strength, false);
        stack.stackTagCompound = tag;
        return stack;
    }

    public Item getSeed() {
        return seed;
    }

    public int getMeta() {
        return meta;
    }

    public double getChance() {
        return chance;
    }

    public int getGain() {
        return gain;
    }

    public int getGrowth() {
        return growth;
    }

    public int getStrength() {
        return strength;
    }

    public void setStats(int growth, int gain, int strength) {
        this.growth = growth;
        this.gain = gain;
        this.strength = strength;
    }
}
