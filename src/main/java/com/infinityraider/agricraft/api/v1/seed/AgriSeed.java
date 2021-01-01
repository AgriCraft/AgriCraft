package com.infinityraider.agricraft.api.v1.seed;

import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneCarrier;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

import com.infinityraider.agricraft.api.v1.stat.IAgriStatProvider;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

/**
 * A simple class for representing seeds. Seeds are immutable objects, for safety reasons.
 */
public final class AgriSeed implements IAgriStatProvider, IAgriGeneCarrier {

    @Nonnull
    private final IAgriPlant plant;
    @Nonnull
    private final IAgriStatsMap stats;
    @Nonnull
    private final IAgriGenome genome;

    public AgriSeed(@Nonnull IAgriPlant plant, @Nonnull IAgriStatsMap stats, @Nonnull IAgriGenome genome) {
        this.plant = Preconditions.checkNotNull(plant, "The plant in an AgriSeed may not be null!");
        this.stats = Preconditions.checkNotNull(stats, "The stat in an AgriSeed may not be null!");
        this.genome = Preconditions.checkNotNull(genome, "The genome in an AgriSeed may not be null!");
    }

    @Nonnull
    public IAgriPlant getPlant() {
        return this.plant;
    }

    @Nonnull
    public IAgriStatsMap getStats() {
        return this.stats;
    }

    @Nonnull
    public AgriSeed withPlant(@Nonnull IAgriPlant plant) {
        return new AgriSeed(plant, stats, genome);
    }

    @Nonnull
    public AgriSeed withStat(@Nonnull IAgriStatsMap stats) {
        return new AgriSeed(plant, stats, genome);
    }

    @Nonnull
    public AgriSeed withGenome(@Nonnull IAgriGenome genome) {
        return new AgriSeed(plant, stats, genome);
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
        final CompoundNBT tag = Optional.ofNullable(stack.getTag())
                .map(CompoundNBT::copy)
                .orElseGet(CompoundNBT::new);

        // Write the stats and genome to the tag.
        if(this.genome.equals(this.stats)) {
            CompoundNBT mixedTag = new CompoundNBT();
            this.stats.writeToNBT(mixedTag);
            tag.put("stats_genome", mixedTag);
        } else {
            CompoundNBT statTag = new CompoundNBT();
            this.stats.writeToNBT(statTag);
            tag.put("stats", statTag);
            CompoundNBT geneTag = new CompoundNBT();
            this.genome.writeToNBT(geneTag);
            tag.put("genome", geneTag);
        }
        // Return a new stack.
        return new ItemStack(stack.getItem(), size, tag);
    }

    @Override
    public final boolean equals(Object obj) {
        return (obj instanceof AgriSeed)
                && (this.equals((AgriSeed) obj));
    }

    public final boolean equals(AgriSeed other) {
        return (other != null)
                && (this.plant.equals(other.plant))
                && (this.stats.equalStats(other.stats))
                && (this.genome.equalGenome(other.genome));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.plant);
        hash = 71 * hash + Objects.hashCode(this.stats);
        hash = 71 * hash + Objects.hashCode(this.genome);
        return hash;
    }

    @Override
    public IAgriGenome getGenome() {
        return this.genome;
    }
}
