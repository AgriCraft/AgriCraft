package com.infinityraider.agricraft.api.v1.seed;

import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneCarrier;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import java.util.Objects;
import javax.annotation.Nonnull;

import com.infinityraider.agricraft.api.v1.stat.IAgriStatProvider;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import net.minecraft.item.ItemStack;

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

    @Override
    public IAgriGenome getGenome() {
        return this.genome;
    }

    @Nonnull
    @Override
    public IAgriStatsMap getStats() {
        return this.genome.getStats();
    }

    @Nonnull
    public ItemStack toStack() {
        // Delegate.
        return toStack(1);
    }

    @Nonnull
    public ItemStack toStack(int size) {
        return AgriApi.seedToStack(this, size);
    }

    @Override
    public final boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
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
}
