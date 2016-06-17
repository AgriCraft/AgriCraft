package com.infinityraider.agricraft.farming.mutation;

import com.infinityraider.agricraft.api.v3.IAgriCraftPlant;
import com.infinityraider.agricraft.api.v3.IAgriCraftStats;
import com.infinityraider.agricraft.api.v3.ICrop;
import com.infinityraider.agricraft.api.v3.IMutation;
import com.infinityraider.agricraft.farming.PlantStats;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Represents the result of a specific <code>INewSeedStrategy</code> containing
 * the seed plus meta and the chance to happen.
 */
public class CrossOverResult {

    private final double chance;

	private @Nonnull IAgriCraftPlant plant;
    private @Nonnull IAgriCraftStats stats = new PlantStats();

    public CrossOverResult(@Nonnull IAgriCraftPlant plant, double chance) {
        this.plant = plant;
        this.chance = chance;
    }

    /** Creates a new instance based on the planted seed of the given TE. Does not validate the TE */
    public static CrossOverResult fromTileEntityCrop(ICrop crop) {
        double chance = ((double) crop.getPlant().getSpreadChance())/100.0;
        return new CrossOverResult(crop.getPlant(), chance);
    }

    /** Creates a new instanced based off the result of the given mutation. Does not validate the mutation object */
    public static CrossOverResult fromMutation(IMutation mutation) {
        return new CrossOverResult(mutation.getChild(), mutation.getChance());
    }

    public ItemStack toStack() {
        ItemStack stack = plant.getSeed();
        NBTTagCompound tag = new NBTTagCompound();
        stats.writeToNBT(tag);
        stack.setTagCompound(tag);
        return stack;
    }

    public @Nonnull IAgriCraftPlant getPlant() {
        return plant;
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
