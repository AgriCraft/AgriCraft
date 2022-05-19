package com.infinityraider.agricraft.impl.v1.genetics;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.*;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatProvider;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatsMap;
import com.infinityraider.agricraft.content.core.ItemDynamicAgriSeed;
import com.infinityraider.agricraft.reference.AgriNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;

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
    public ItemStack toSeedStack(int amount) {
        return ItemDynamicAgriSeed.toStack(this, amount);
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
    public boolean writeToNBT(@Nonnull CompoundTag tag) {
        ListTag list = new ListTag();
        this.geneMap.values().stream()
                .sorted(Comparator.comparing(a -> a.getGene().getId()))
                .forEach(pair -> {
                    CompoundTag geneTag = new CompoundTag();
                    geneTag.putString(AgriNBT.GENE, pair.getGene().getId());
                    geneTag.put(AgriNBT.DOMINANT, pair.getDominant().writeToNBT());
                    geneTag.put(AgriNBT.RECESSIVE, pair.getRecessive().writeToNBT());
                    list.add(geneTag);
                });
        tag.put(AgriNBT.GENOME, list);
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean readFromNBT(@Nonnull CompoundTag tag) {
        if(tag.contains(AgriNBT.GENOME)) {
            ListTag list = tag.getList(AgriNBT.GENOME, 10);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag geneTag = list.getCompound(i);
                AgriGeneRegistry.getInstance().get(geneTag.getString(AgriNBT.GENE))
                        .ifPresent(gene -> {
                            this.geneMap.put(gene, this.generateGenePairFromNBT(gene, geneTag));
                        });
            }
            return this.getPlant().isPlant();
        }
        return false;
    }

    private <T> IAgriGenePair<T> generateGenePairFromNBT(IAgriGene<T> gene, CompoundTag tag) {
        IAllele<T> dominant = gene.readAlleleFromNBT(tag.getCompound(AgriNBT.DOMINANT));
        IAllele<T> recessive = gene.readAlleleFromNBT(tag.getCompound(AgriNBT.RECESSIVE));
        return  gene.generateGenePair(dominant, recessive);
    }

    @Override
    public int getValue(IAgriStat stat) {
        return AgriGeneRegistry.getInstance().get(stat)
                .map(this::getGenePair)
                .map(IAgriGenePair::getTrait)
                .orElse(stat.getMin());
    }

    public static IAgriGenome.Builder builder(IAgriPlant plant) {
        return new Builder(plant);
    }

    @Override
    public void addDisplayInfo(@Nonnull Consumer<Component> consumer) {
        this.geneMap.values().forEach(genePair -> genePair.addTooltipDescription(consumer));
    }

    private static class Builder implements IAgriGenome.Builder {
        private final Map<IAgriGene<?>, IAgriGenePair<?>> geneMap;

        private Builder(IAgriPlant plant) {
            this.geneMap = Maps.newIdentityHashMap();
            for(IAgriGene<?> gene : AgriApi.getGeneRegistry().all()) {
                this.geneMap.put(gene, this.generateDefaultPair(gene, plant));
            }
            this.geneMap.put(GeneSpecies.getInstance(), GeneSpecies.getInstance().generateGenePair(plant, plant));
        }

        private <T> IAgriGenePair<T> generateDefaultPair(IAgriGene<T> gene, IAgriPlant plant) {
            return gene.generateGenePair(gene.defaultAllele(plant), gene.defaultAllele(plant));
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
