package com.infinityraider.agricraft.impl.v1.genetics;

import com.infinityraider.agricraft.api.v1.genetics.IAgriGene;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.api.v1.genetics.IAllel;

public class AgriGenePair<A> implements IAgriGenePair<A> {
    private final IAgriGene<A> gene;
    private final IAllel<A> dominant;
    private final IAllel<A> recessive;

    public AgriGenePair(IAgriGene<A> gene, IAllel<A> first, IAllel<A> second) {
        this.gene = gene;
        if (first.isDominant(second)) {
            this.dominant = first;
            this.recessive = second;
        } else {
            this.dominant = second;
            this.recessive = first;
        }
    }

    @Override
    public final IAgriGene<A> getGene() {
        return this.gene;
    }

    @Override
    public final IAllel<A> getDominant() {
        return this.dominant;
    }

    @Override
    public final IAllel<A> getRecessive() {
        return this.recessive;
    }

    @Override
    public IAgriGenePair<A> clone() {
        return new AgriGenePair<>(this.getGene(), this.getDominant(), this.getRecessive());
    }
}
