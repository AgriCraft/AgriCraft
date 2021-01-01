package com.infinityraider.agricraft.core.genetics;

import com.infinityraider.agricraft.api.v1.genetics.*;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.core.stats.AgriStatRegistry;
import com.infinityraider.agricraft.impl.v1.AgriRegistry;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("Unchecked")
public class AgriGeneRegistry extends AgriRegistry<IAgriGene<?>> implements IAgriGeneRegistry {
    private static final AgriGeneRegistry INSTANCE = new AgriGeneRegistry();

    public static AgriGeneRegistry getInstance() {
        return INSTANCE;
    }

    public final IAgriGene<IAgriPlant> gene_species;

    private AgriGeneRegistry() {
        super("gene", getGeneClass());
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

    @Override
    @SuppressWarnings("unchecked")
    public Optional<IAgriGene<Byte>> get(@Nullable IAgriStat stat) {
        return stat == null ? Optional.empty() : this.get(stat.getId()).map(gene -> (IAgriGene<Byte>) gene);
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
    public Optional<IAgriStat> getStatForGene(@Nullable IAgriGene<Byte> gene) {
        return Optional.ofNullable(gene).flatMap(g -> AgriStatRegistry.getInstance().get(g.getId()));
    }

    // I have no idea how else I can get the generics to work here...
    @SuppressWarnings("Unchecked")
    private static Class<IAgriGene<?>> getGeneClass() {
        IAgriGene<Object> temp = new IAgriGene<Object>() {
            @Override
            public IAllel<Object> defaultAllel() {
                return null;
            }

            @Override
            public IAllel<Object> getAllel(Object value) {
                return null;
            }

            @Override
            public IAllel<Object> readAllelFromNBT(CompoundNBT tag) {
                return null;
            }

            @Override
            public Set<IAllel<Object>> allAlleles() {
                return null;
            }

            @Override
            public IMutator<Object> mutator() {
                return null;
            }

            @Override
            public IAgriGenePair<Object> generateGenePair(IAllel<Object> first, IAllel<Object> second) {
                return null;
            }

            @Nonnull
            @Override
            public String getId() {
                return null;
            }
        };
        return (Class<IAgriGene<?>>) temp.getClass();
    }
}
