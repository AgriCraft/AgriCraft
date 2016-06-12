package com.infinityraider.agricraft.farming.mutation;

import com.infinityraider.agricraft.api.v1.IAgriCraftStats;
import com.infinityraider.agricraft.farming.PlantStats;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import javax.annotation.Nonnull;
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

    private @Nonnull IAgriCraftStats stats = new PlantStats();

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
        stats.writeToNBT(tag);
        stack.setTagCompound(tag);
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

    public @Nonnull IAgriCraftStats getStats() {
		return this.stats;
	}

    public void setStats(@Nonnull IAgriCraftStats stats) {
        this.stats = stats;
    }
}
