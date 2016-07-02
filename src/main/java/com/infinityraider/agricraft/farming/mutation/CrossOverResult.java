package com.infinityraider.agricraft.farming.mutation;

import com.infinityraider.agricraft.farming.PlantStats;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.mutation.IAgriMutation;
import com.infinityraider.agricraft.apiimpl.v1.StatRegistry;

/**
 * Represents the result of a specific <code>INewSeedStrategy</code> containing
 * the seed plus meta and the chance to happen.
 */
public class CrossOverResult {

    private final double chance;

	private @Nonnull IAgriPlant plant;
    private @Nonnull IAgriStat stats = new PlantStats();

    public CrossOverResult(@Nonnull IAgriPlant plant, double chance) {
        this.plant = plant;
        this.chance = chance;
    }

    /** Creates a new instance based on the planted seed of the given TE. Does not validate the TE */
    public static CrossOverResult fromTileEntityCrop(IAgriCrop crop) {
        double chance = ((double) crop.getPlant().getSpreadChance())/100.0;
        return new CrossOverResult(crop.getPlant(), chance);
    }

    /** Creates a new instanced based off the result of the given mutation. Does not validate the mutation object */
    public static CrossOverResult fromMutation(IAgriMutation mutation) {
        return new CrossOverResult(mutation.getChild(), mutation.getChance());
    }

    public ItemStack toStack() {
        ItemStack stack = plant.getSeed();
        NBTTagCompound tag = new NBTTagCompound();
        StatRegistry.getInstance().setStat(tag, stats);
        stack.setTagCompound(tag);
        return stack;
    }

    public @Nonnull IAgriPlant getPlant() {
        return plant;
    }

    public double getChance() {
        return chance;
    }

    public @Nonnull IAgriStat getStats() {
		return this.stats;
	}

    public void setStats(@Nonnull IAgriStat stats) {
        this.stats = stats;
    }
}
