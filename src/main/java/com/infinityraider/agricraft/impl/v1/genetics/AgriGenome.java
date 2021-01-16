package com.infinityraider.agricraft.impl.v1.genetics;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.*;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatProvider;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import com.infinityraider.agricraft.reference.AgriNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

import javax.annotation.Nonnull;
import java.util.Map;

public class AgriGenome implements IAgriGenome, IAgriStatsMap, IAgriStatProvider {
    private final Map<IAgriGene<?>, IAgriGenePair<?>> geneMap;

    private AgriGenome(Map<IAgriGene<?>, IAgriGenePair<?>> geneMap) {
        this.geneMap = geneMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> IAgriGenePair<T> getGenePair(IAgriGene<T> gene) {
        return (IAgriGenePair<T>) this.geneMap.get(gene);
    }

    @Override
    public IAgriStatsMap getStats() {
        return this;
    }

    @Override
    public IAgriGenome clone() {
        return new AgriGenome(Maps.transformValues(this.geneMap, IAgriGenePair::clone));
    }

    @Override
    public boolean writeToNBT(@Nonnull CompoundNBT tag) {
        ListNBT list = new ListNBT();
        int index = 0;
        for(IAgriGenePair<?> pair : this.geneMap.values()) {
            CompoundNBT geneTag = new CompoundNBT();
            geneTag.putString(AgriNBT.GENE, pair.getGene().getId());
            geneTag.put(AgriNBT.DOMINANT, pair.getDominant().writeToNBT());
            geneTag.put(AgriNBT.RECESSIVE, pair.getRecessive().writeToNBT());
            list.addNBTByIndex(index, geneTag);
            index++;
        }
        tag.put(AgriNBT.GENOME, list);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean readFromNBT(@Nonnull CompoundNBT tag) {
        if(tag.contains(AgriNBT.GENOME)) {
            ListNBT list = tag.getList(AgriNBT.GENOME, 9);
            for (int i = 0; i < list.size(); i++) {
                CompoundNBT geneTag = list.getCompound(i);
                AgriGeneRegistry.getInstance().get(geneTag.getString(AgriNBT.GENE))
                        .ifPresent(gene -> {
                            this.geneMap.put(gene, this.generateGenePairFromNBT(gene, tag));
                        });
            }
            return true;
        }
        return false;
    }

    private <T> IAgriGenePair<T> generateGenePairFromNBT(IAgriGene<T> gene, CompoundNBT tag) {
        IAllel<T> dominant = gene.readAllelFromNBT(tag.getCompound(AgriNBT.DOMINANT));
        IAllel<T> recessive = gene.readAllelFromNBT(tag.getCompound(AgriNBT.RECESSIVE));
        return  gene.generateGenePair(dominant, recessive);
    }

    @Override
    public int getValue(IAgriStat stat) {
        return AgriGeneRegistry.getInstance().get(stat)
                .map(this::getGenePair)
                .map(pair -> pair.getDominant().trait())
                .orElse(stat.getMin());
    }

    public static IAgriGenome.Builder builder(IAgriPlant plant) {
        return new Builder(plant);
    }

    private static class Builder implements IAgriGenome.Builder {
        private final Map<IAgriGene<?>, IAgriGenePair<?>> geneMap;

        private Builder(IAgriPlant plant) {
            this.geneMap = Maps.newIdentityHashMap();
            for(IAgriGene<?> gene : AgriApi.getGeneRegistry().all()) {
                this.geneMap.put(gene, this.generateDefaultPair(gene));
            }
            this.geneMap.put(GeneSpecies.getInstance(), GeneSpecies.getInstance().generateGenePair(plant, plant));
        }

        private <T> IAgriGenePair<T> generateDefaultPair(IAgriGene<T> gene) {
            return gene.generateGenePair(gene.defaultAllel(), gene.defaultAllel());
        }

        @Override
        public IAgriGenome build() {
            return new AgriGenome(this.geneMap);
        }

        @Override
        public <T> IAgriGenome.Builder put(IAgriGenePair<T> pair) {
            this.geneMap.put(pair.getGene(), pair);
            return this;
        }
    }
}
