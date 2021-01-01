package com.infinityraider.agricraft.core.genetics;

import com.google.common.collect.ImmutableSet;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGene;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.api.v1.genetics.IAllel;
import com.infinityraider.agricraft.api.v1.genetics.IMutator;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.core.mutation.AgriMutationHandler;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.Set;

public class GeneStat implements IAgriGene<Byte> {
    private final IAgriStat stat;
    private final IAllel<Byte> defaultAllel;
    private final Set<IAllel<Byte>> alleles;

    public GeneStat(IAgriStat stat) {
        this.stat = stat;
        this.defaultAllel = new StatAllel(this, this.getStat().getMin());
        ImmutableSet.Builder<IAllel<Byte>> builder = ImmutableSet.builder();
        for(byte i = this.getStat().getMin(); i <= this.getStat().getMax(); i++) {
            builder.add(i == this.getStat().getMin() ? this.defaultAllel : new StatAllel(this, i));
        }
        this.alleles = builder.build();
    }

    public IAgriStat getStat() {
        return this.stat;
    }

    @Override
    public IAllel<Byte> defaultAllel() {
        return this.defaultAllel;
    }

    @Override
    public IAllel<Byte> getAllel(Byte value) {
        return this.allAlleles().stream()
                .filter(allel -> allel.trait() == value.byteValue())
                .findFirst().orElse(this.defaultAllel());
    }

    @Override
    public IAllel<Byte> readAllelFromNBT(CompoundNBT tag) {
        return this.getAllel(tag.getByte(this.getStat().getId()));
    }

    @Override
    public Set<IAllel<Byte>> allAlleles() {
        return this.alleles;
    }

    @Override
    public IMutator<Byte> mutator() {
        return AgriMutationHandler.getInstance().getActiveStatMutator();
    }

    @Override
    public IAgriGenePair<Byte> generateGenePair(IAllel<Byte> first, IAllel<Byte> second) {
        return new AgriGenePair<>(this, first, second);
    }

    @Nonnull
    @Override
    public String getId() {
        return this.stat.getId();
    }

    private static final class StatAllel implements IAllel<Byte> {
        private final GeneStat gene;
        private final byte value;

        private StatAllel(GeneStat gene, byte value) {
            this.gene = gene;
            this.value = value;
        }

        @Override
        public GeneStat gene() {
            return this.gene;
        }

        @Override
        public Byte trait() {
            return this.value;
        }

        @Override
        public boolean isDominant(IAllel<Byte> other) {
            return this.trait() >= other.trait();
        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            tag.putByte(this.gene().getStat().getId(), this.trait());
            return tag;
        }
    }
}
