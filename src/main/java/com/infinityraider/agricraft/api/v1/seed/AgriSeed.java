package com.infinityraider.agricraft.api.v1.seed;

import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.api.v1.AgriApi;
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
    private final IAgriGenome genome;

    public AgriSeed(@Nonnull IAgriPlant plant) {
        this(AgriApi.getAgriGenomeBuilder(Preconditions.checkNotNull(plant, "The plant in an AgriSeed may not be null!")).build());
    }

    public AgriSeed(@Nonnull IAgriGenome genome) {
        this.genome = Preconditions.checkNotNull(genome, "The genome in an AgriSeed may not be null!");
    }

    @Nonnull
    public IAgriPlant getPlant() {
        return this.genome.getTrait(AgriApi.getGeneRegistry().getPlantGene()).trait();
    }

    @Nonnull
    public ItemStack toStack() {
        // Delegate.
        return toStack(1);
    }

    @Nonnull
    public ItemStack toStack(int size) {
        // Get the stack.
        final ItemStack stack = Preconditions.checkNotNull(this.getPlant().getSeed());
        // Get the tag.
        final CompoundNBT tag = Optional.ofNullable(stack.getTag())
                .map(CompoundNBT::copy)
                .orElseGet(CompoundNBT::new);
        // Write the genome to the tag.
        CompoundNBT geneTag = new CompoundNBT();
        this.genome.writeToNBT(geneTag);
        tag.put("genome", geneTag);
        // Return a new stack.
        ItemStack ret = new ItemStack(stack.getItem(), size);
        ret.setTag(tag);
        return ret;
    }

    @Override
    public final boolean equals(Object obj) {
        return (obj instanceof AgriSeed)
                && (this.equals((AgriSeed) obj));
    }

    public final boolean equals(AgriSeed other) {
        return (other != null) && (this.genome.equalGenome(other.genome));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.genome);
        return hash;
    }

    @Override
    public Optional<IAgriGenome> getGenome() {
        return Optional.of(this.genome);
    }

    @Override
    public void setGenome(@Nonnull IAgriGenome genome) {
        // UNSUPPORTED
    }

    @Nonnull
    @Override
    public IAgriStatsMap getStats() {
        return this.genome.getStats();
    }
}
