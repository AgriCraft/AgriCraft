package com.infinityraider.agricraft.content.tools;

import com.google.common.collect.Lists;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.impl.v1.genetics.AgriGeneRegistry;
import com.infinityraider.agricraft.impl.v1.genetics.GeneSpecies;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.IItemHandler;

import java.util.Comparator;
import java.util.List;

public class ItemSeedBag extends ItemBase {
    private static final List<ISorter> sorters = Lists.newArrayList();

    public static ISorter getSorter(int index) {
        return sorters.get(index % sorters.size());
    }

    public static int addSorter(ISorter sorter) {
        if(sorters.contains(sorter)) {
            return sorters.indexOf(sorter);
        } else {
            sorters.add(sorter);
            return sorters.size() - 1;
        }
    }

    public static int addSorter(IAgriStat stat) {
        return addSorter(new StatSorter(stat));
    }

    public ItemSeedBag() {
        super(Names.Items.SEED_BAG, new Properties()
                .group(AgriTabs.TAB_AGRICRAFT)
                .maxStackSize(1)
        );
    }

    public interface IContents extends IItemHandler {
        IAgriPlant getPlant();

        int getCount();

        ISorter getSorter();

        int getSorterIndex();

        void setSorterIndex(int index);
    }

    public interface ISorter extends Comparator<IAgriGenome> {
        ITextComponent getName();
    }

    public static final ISorter DEFAULT_SORTER = new ISorter() {
        @Override
        public ITextComponent getName() {
            return AgriToolTips.TOTAL;
        }

        @Override
        public int compare(IAgriGenome o1, IAgriGenome o2) {
            int s1 = o1.getStats().getSum();
            int s2 = o2.getStats().getSum();
            if(s1 != s2) {
                return s1 - s2;
            }
            return AgriGeneRegistry.getInstance().stream()
                    .filter(gene -> !(gene instanceof GeneSpecies))
                    .mapToInt(gene -> {
                        int w = gene.getComparatorWeight();
                        int d1 = o1.getGenePair(gene).getDominant().comparatorValue();
                        int r1 = o1.getGenePair(gene).getRecessive().comparatorValue();
                        int d2 = o2.getGenePair(gene).getDominant().comparatorValue();
                        int r2 = o2.getGenePair(gene).getRecessive().comparatorValue();
                        return w*((d1 + r1) - (d2 + r2));
                    }).sum();
        }
    };

    public static class StatSorter implements ISorter {
        private final IAgriStat stat;

        protected StatSorter(IAgriStat stat) {
            this.stat = stat;
        }

        public IAgriStat getStat() {
            return this.stat;
        }

        @Override
        public ITextComponent getName() {
            return this.getStat().getDescription();
        }

        @Override
        public int compare(IAgriGenome o1, IAgriGenome o2) {
            int s1 = o1.getStats().getValue(this.getStat());
            int s2 = o2.getStats().getValue(this.getStat());
            if(s1 == s2) {
                return DEFAULT_SORTER.compare(o1, o2);
            }
            return s1 - s2;
        }
    }

    static {
        addSorter(DEFAULT_SORTER);
    }
}
