package com.infinityraider.agricraft.impl.v1.genetics;

import com.infinityraider.agricraft.api.v1.genetics.IAgriGene;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.api.v1.genetics.IAllele;
import com.infinityraider.agricraft.reference.AgriToolTips;
import net.minecraft.util.text.ITextComponent;

import java.util.function.Consumer;

public class AgriGenePair<A> implements IAgriGenePair<A> {
    private final IAgriGene<A> gene;
    private final IAllele<A> dominant;
    private final IAllele<A> recessive;

    public AgriGenePair(IAgriGene<A> gene, IAllele<A> first, IAllele<A> second) {
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
    public IAgriGene<A> getGene() {
        return this.gene;
    }

    @Override
    public final IAllele<A> getDominant() {
        return this.dominant;
    }

    @Override
    public final IAllele<A> getRecessive() {
        return this.recessive;
    }

    @Override
    public IAgriGenePair<A> clone() {
        return new AgriGenePair<>(this.getGene(), this.getDominant(), this.getRecessive());
    }

    @Override
    public void addTooltipDescription(Consumer<ITextComponent> consumer) {
        consumer.accept(AgriToolTips.getGeneTooltip(this));
    }
}
