package com.infinityraider.agricraft.impl.v1.genetics;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGene;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.api.v1.genetics.IAllele;
import com.infinityraider.agricraft.api.v1.genetics.IMutator;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.reference.AgriToolTips;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntBinaryOperator;

public class GeneStat implements IAgriGene<Integer> {
    private final IAgriStat stat;
    private final Map<Integer, Allele> alleleMap;
    private final Set<IAllele<Integer>> allelesSet; // Need a separate set due to generics issues
    private final Vector3f colorDominant;
    private final Vector3f colorRecessive;

    public GeneStat(IAgriStat stat) {
        this.stat = stat;
        ImmutableMap.Builder<Integer, Allele> mapBuilder = ImmutableMap.builder();
        ImmutableSet.Builder<IAllele<Integer>> setBuilder = ImmutableSet.builder();
        for(int i = this.getStat().getMin(); i <= this.getStat().getMax(); i++) {
            Allele allele = new Allele(this, i);
            mapBuilder.put(i, allele);
            setBuilder.add(allele);
        }
        this.alleleMap = mapBuilder.build();
        this.allelesSet = setBuilder.build();
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

    public Allele defaultAllele() {
        return this.alleleMap.get(this.getMin());
    }

    @Nonnull
    @Override
    public Allele defaultAllele(IAgriPlant plant) {
        return this.defaultAllele();
    }

    @Nonnull
    @Override
    public Allele getAllele(Integer value) {
        final int val = Math.max(this.getMin(), Math.min(this.getMax(), value));
        return this.alleleMap.get(val);
    }

    @Nonnull
    @Override
    public Allele readAlleleFromNBT(@Nonnull CompoundNBT tag) {
        return this.getAllele(tag.getInt(this.getStat().getId()));
    }

    @Nonnull
    @Override
    public Set<IAllele<Integer>> allAlleles() {
        return this.allelesSet;
    }

    @Nonnull
    @Override
    public IMutator<Integer> mutator() {
        return AgriMutationHandler.getInstance().getActiveStatMutator();
    }

    @Nonnull
    @Override
    public IAgriGenePair<Integer> generateGenePair(IAllele<Integer> first, IAllele<Integer> second) {
        return new GenePair(this,
                first instanceof Allele ? (Allele) first : this.getAllele(first.trait()),
                second instanceof Allele ? (Allele) second : this.getAllele(second.trait())
        );
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

    @Override
    public int getComparatorWeight() {
        return 10;
    }

    @Override
    public boolean isHidden() {
        return this.getStat().isHidden();
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

    private static final class Allele implements IAllele<Integer> {
        private final GeneStat gene;
        private final int value;
        private final StringTextComponent tooltip;

        private Allele(GeneStat gene, int value) {
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
        public int comparatorValue() {
            return this.trait();
        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            tag.putInt(this.gene().getStat().getId(), this.trait());
            return tag;
        }
    }

    private static final class GenePair implements IAgriGenePair<Integer> {
        private final GeneStat gene;
        private final Allele dominant;
        private final Allele recessive;

        public GenePair(GeneStat gene, Allele first, Allele second) {
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
        public GeneStat getGene() {
            return this.gene;
        }

        @Override
        public Integer getTrait() {
            return getLogic(AgriCraft.instance.getConfig().getStatTraitLogic()).apply(this.getDominant().trait(), this.getRecessive().trait());
        }

        @Override
        public Allele getDominant() {
            return this.dominant;
        }

        @Override
        public Allele getRecessive() {
            return this.recessive;
        }

        @Override
        public IAgriGenePair<Integer> clone() {
            return new GenePair(this.getGene(), this.getDominant(), this.getRecessive());
        }

        @Override
        public void addTooltipDescription(Consumer<ITextComponent> consumer) {
            if(!this.getGene().isHidden()) {
                consumer.accept(AgriToolTips.getGeneTooltip(this));
            }
        }
    }

    private static final Map<String, Logic> LOGIC_MAP = Maps.newHashMap();

    static {
        Arrays.stream(Logic.values()).forEach(logic -> LOGIC_MAP.put(logic.name().toLowerCase(), logic));
    }

    public enum Logic {
        MIN(Math::min),
        MEAN((a, b) -> (a + b)/2),
        MAX(Math::max);
        private final IntBinaryOperator function;

        Logic(IntBinaryOperator function) {
            this.function = function;
        }

        public int apply(int a, int b) {
            return this.function.applyAsInt(a, b);
        }
    }

    public static List<String> getLogicOptions() {
        return ImmutableList.copyOf(LOGIC_MAP.keySet());
    }

    public static Logic getLogic(String value) {
        return LOGIC_MAP.getOrDefault(value.toLowerCase(), Logic.MAX);
    }

}
