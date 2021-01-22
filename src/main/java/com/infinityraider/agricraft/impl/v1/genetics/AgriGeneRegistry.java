package com.infinityraider.agricraft.impl.v1.genetics;

import com.infinityraider.agricraft.api.v1.genetics.*;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.impl.v1.stats.AgriStatRegistry;
import com.infinityraider.agricraft.impl.v1.AgriRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

@SuppressWarnings("Unchecked")
public class AgriGeneRegistry extends AgriRegistry<IAgriGene<?>> implements IAgriGeneRegistry {
    private static final AgriGeneRegistry INSTANCE = new AgriGeneRegistry();

    public static AgriGeneRegistry getInstance() {
        return INSTANCE;
    }

    public final IAgriGene<IAgriPlant> gene_species;

    private AgriGeneRegistry() {
        super();
        // Auto populate species gene
        this.gene_species = GeneSpecies.getInstance();
        this.add(gene_species);
    }

    @Override
    public boolean add(@Nullable IAgriGene<?> element) {
        if(element instanceof GeneStat) {
            // In case a gene stat is added, forward it to the method which handles it correctly instead
            return this.addGeneForStat(((GeneStat) element).getStat());
        }
        return super.add(element);
    }

    @Override
    public boolean remove(@Nullable IAgriGene<?> element) {
        if(element == gene_species) {
            // We do not allow removing the species gene
            return false;
        }
        if(element instanceof GeneStat) {
            // In case a gene stat is removed, forward it to the method which handles it correctly instead
            return this.removeGeneForStat(((GeneStat) element).getStat());
        }
        return super.remove(element);
    }

    @Nonnull
    @Override
    public IAgriGene<IAgriPlant> getPlantGene() {
        return this.gene_species;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<IAgriGene<Integer>> get(@Nullable IAgriStat stat) {
        return stat == null ? Optional.empty() : this.get(stat.getId()).map(gene -> (IAgriGene<Integer>) gene);
    }

    public boolean addGeneForStat(@Nullable IAgriStat stat) {
        return this.get(stat).map(gene -> {
            // The gene for this stat is already present
            return false;
        }).orElseGet(() -> {
            boolean result = super.add(new GeneStat(stat));
            if(result) {
                // Make sure that the stat is also added
                AgriStatRegistry.getInstance().add(stat);
            }
            return result;
        });
    }

    public boolean removeGeneForStat(@Nullable IAgriStat stat) {
        return this.get(stat).map(gene -> {
            boolean result = super.remove(gene);
            if(result) {
                // Make sure that the stat is also removed
                AgriStatRegistry.getInstance().remove(stat);
            }
            return result;
        }).orElse(false);
    }

    @Override
    public Optional<IAgriStat> getStatForGene(@Nullable IAgriGene<Integer> gene) {
        return Optional.ofNullable(gene).flatMap(g -> AgriStatRegistry.getInstance().get(g.getId()));
    }
}
