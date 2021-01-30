package com.infinityraider.agricraft.impl.v1.genetics;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGene;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.api.v1.genetics.IAllele;
import com.infinityraider.agricraft.api.v1.genetics.IMutator;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import java.util.Set;

public class GeneStat implements IAgriGene<Integer> {
    private final IAgriStat stat;
    private final IAllele<Integer> defaultAllele;
    private final Set<IAllele<Integer>> alleles;

    public GeneStat(IAgriStat stat) {
        this.stat = stat;
        this.defaultAllele = new StatAllele(this, this.getStat().getMin());
        ImmutableSet.Builder<IAllele<Integer>> builder = ImmutableSet.builder();
        for(int i = this.getStat().getMin(); i <= this.getStat().getMax(); i++) {
            builder.add(i == this.getStat().getMin() ? this.defaultAllele : new StatAllele(this, i));
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

    public IAllele<Integer> defaultAllele() {
        return this.defaultAllele;
    }

    @Override
    public IAllele<Integer> defaultAllele(IAgriPlant plant) {
        return this.defaultAllele();
    }

    @Override
    public IAllele<Integer> getAllele(Integer value) {
        final int val = Math.max(this.getMin(), Math.min(this.getMax(), value));
        return this.allAlleles().stream()
                .filter(allel -> allel.trait() == val)
                .findFirst().orElse(this.defaultAllele());
    }

    @Override
    public IAllele<Integer> readAlleleFromNBT(CompoundNBT tag) {
        return this.getAllele(tag.getInt(this.getStat().getId()));
    }

    @Override
    public Set<IAllele<Integer>> allAlleles() {
        return this.alleles;
    }

    @Override
    public IMutator<Integer> mutator() {
        return AgriMutationHandler.getInstance().getActiveStatMutator();
    }

    @Override
    public IAgriGenePair<Integer> generateGenePair(IAllele<Integer> first, IAllele<Integer> second) {
        return new AgriGenePair<>(this, first, second);
    }

    @Override
    public ITextComponent getDescription() {
        return this.getStat().getDescription();
    }

    @Nonnull
    @Override
    public String getId() {
        return this.stat.getId();
    }

    private static final class StatAllele implements IAllele<Integer> {
        private final GeneStat gene;
        private final int value;
        private final ITextComponent tooltip;

        private StatAllele(GeneStat gene, int value) {
            this.gene = gene;
            this.value = value;
            this.tooltip = new StringTextComponent("" + this.trait());
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
        public boolean isDominant(IAllele<Integer> other) {
            return this.trait() >= other.trait();
        }

        @Override
        public ITextComponent getTooltip() {
            // TODO: format tooltip according to config
            return this.tooltip;
        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            tag.putInt(this.gene().getStat().getId(), this.trait());
            return tag;
        }
    }
}
