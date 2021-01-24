package com.infinityraider.agricraft.impl.v1.genetics;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGene;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.api.v1.genetics.IAllel;
import com.infinityraider.agricraft.api.v1.genetics.IMutator;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.Set;

public class GeneStat implements IAgriGene<Integer> {
    private final IAgriStat stat;
    private final IAllel<Integer> defaultAllel;
    private final Set<IAllel<Integer>> alleles;

    public GeneStat(IAgriStat stat) {
        this.stat = stat;
        this.defaultAllel = new StatAllel(this, this.getStat().getMin());
        ImmutableSet.Builder<IAllel<Integer>> builder = ImmutableSet.builder();
        for(int i = this.getStat().getMin(); i <= this.getStat().getMax(); i++) {
            builder.add(i == this.getStat().getMin() ? this.defaultAllel : new StatAllel(this, i));
        }
        this.alleles = builder.build();
    }

    public IAgriStat getStat() {
        return this.stat;
    }

    public int getMin() {
        return this.getStat().getMin();
    }

    public int getMax() {
        return this.getStat().getMax();
    }

    public IAllel<Integer> defaultAllel() {
        return this.defaultAllel;
    }

    @Override
    public IAllel<Integer> defaultAllel(IAgriPlant plant) {
        return this.defaultAllel();
    }

    @Override
    public IAllel<Integer> getAllel(Integer value) {
        final int val = Math.max(this.getMin(), Math.min(this.getMax(), value));
        return this.allAlleles().stream()
                .filter(allel -> allel.trait() == val)
                .findFirst().orElse(this.defaultAllel());
    }

    @Override
    public IAllel<Integer> readAllelFromNBT(CompoundNBT tag) {
        return this.getAllel(tag.getInt(this.getStat().getId()));
    }

    @Override
    public Set<IAllel<Integer>> allAlleles() {
        return this.alleles;
    }

    @Override
    public IMutator<Integer> mutator() {
        return AgriMutationHandler.getInstance().getActiveStatMutator();
    }

    @Override
    public IAgriGenePair<Integer> generateGenePair(IAllel<Integer> first, IAllel<Integer> second) {
        return new AgriGenePair<>(this, first, second);
    }

    @Nonnull
    @Override
    public String getId() {
        return this.stat.getId();
    }

    private static final class StatAllel implements IAllel<Integer> {
        private final GeneStat gene;
        private final int value;

        private StatAllel(GeneStat gene, int value) {
            this.gene = gene;
            this.value = value;
        }

        @Override
        public GeneStat gene() {
            return this.gene;
        }

        @Override
        public Integer trait() {
            return this.value;
        }

        @Override
        public boolean isDominant(IAllel<Integer> other) {
            return this.trait() >= other.trait();
        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            tag.putInt(this.gene().getStat().getId(), this.trait());
            return tag;
        }
    }
}
