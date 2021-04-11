package com.infinityraider.agricraft.impl.v1.genetics;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGene;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.api.v1.genetics.IAllele;
import com.infinityraider.agricraft.api.v1.genetics.IMutator;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import java.util.Set;

public class GeneStat implements IAgriGene<Integer> {
    private final IAgriStat stat;
    private final IAllele<Integer> defaultAllele;
    private final Set<IAllele<Integer>> alleles;
    private final Vector3f colorDominant;
    private final Vector3f colorRecessive;

    public GeneStat(IAgriStat stat) {
        this.stat = stat;
        this.defaultAllele = new StatAllele(this, this.getStat().getMin());
        ImmutableSet.Builder<IAllele<Integer>> builder = ImmutableSet.builder();
        for(int i = this.getStat().getMin(); i <= this.getStat().getMax(); i++) {
            builder.add(i == this.getStat().getMin() ? this.defaultAllele : new StatAllele(this, i));
        }
        this.alleles = builder.build();
        this.colorDominant = stat.getColor();
        this.colorRecessive = getRecessiveVector(stat.getColor());
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

    @Nonnull
    @Override
    public IAllele<Integer> defaultAllele(IAgriPlant plant) {
        return this.defaultAllele();
    }

    @Nonnull
    @Override
    public IAllele<Integer> getAllele(Integer value) {
        final int val = Math.max(this.getMin(), Math.min(this.getMax(), value));
        return this.allAlleles().stream()
                .filter(allel -> allel.trait() == val)
                .findFirst().orElse(this.defaultAllele());
    }

    @Nonnull
    @Override
    public IAllele<Integer> readAlleleFromNBT(@Nonnull CompoundNBT tag) {
        return this.getAllele(tag.getInt(this.getStat().getId()));
    }

    @Nonnull
    @Override
    public Set<IAllele<Integer>> allAlleles() {
        return this.alleles;
    }

    @Nonnull
    @Override
    public IMutator<Integer> mutator() {
        return AgriMutationHandler.getInstance().getActiveStatMutator();
    }

    @Nonnull
    @Override
    public IAgriGenePair<Integer> generateGenePair(IAllele<Integer> first, IAllele<Integer> second) {
        return new AgriGenePair<>(this, first, second);
    }

    @Nonnull
    @Override
    public IFormattableTextComponent getGeneDescription() {
        return this.getStat().getDescription();
    }

    @Nonnull
    @Override
    public Vector3f getDominantColor() {
        return this.colorDominant;
    }

    @Nonnull
    @Override
    public Vector3f getRecessiveColor() {
        return this.colorRecessive;
    }

    @Nonnull
    @Override
    public String getId() {
        return this.stat.getId();
    }

    private static final float COLOR_FACTOR = 0.60F;

    protected static Vector3f getRecessiveVector(Vector3f dominant) {
        return new Vector3f(
                COLOR_FACTOR * dominant.getX(),
                COLOR_FACTOR * dominant.getY(),
                COLOR_FACTOR * dominant.getZ()
        );
    }

    private static final class StatAllele implements IAllele<Integer> {
        private final GeneStat gene;
        private final int value;
        private final StringTextComponent tooltip;

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
        public StringTextComponent getTooltip() {
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
