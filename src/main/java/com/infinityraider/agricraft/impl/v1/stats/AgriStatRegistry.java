package com.infinityraider.agricraft.impl.v1.stats;

import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import com.infinityraider.agricraft.impl.v1.genetics.AgriGeneRegistry;
import com.infinityraider.agricraft.impl.v1.AgriRegistry;

import javax.annotation.Nullable;

public class AgriStatRegistry extends AgriRegistry<IAgriStat> implements IAgriStatRegistry {
    private static final AgriStatRegistry INSTANCE = new AgriStatRegistry();

    public static final AgriStatRegistry getInstance() {
        return INSTANCE;
    }

    public static final int MIN = 1;
    public static final int MAX = 10;

    private final IAgriStat gain;
    private final IAgriStat growth;
    private final IAgriStat strength;
    private final IAgriStat resistance;
    private final IAgriStat fertility;
    private final IAgriStat mutativity;

    private AgriStatRegistry() {
        //super constructor
        super("stats", IAgriStat.class);
        //initiate default stats
        this.gain = new AgriStat("gain", MIN, MAX);
        this.growth = new AgriStat("growth", MIN, MAX);
        this.strength = new AgriStat("strength", MIN, MAX);
        this.resistance = new AgriStat("resistance", MIN, MAX);
        this.fertility = new AgriStat("fertility", MIN, MAX);
        this.mutativity = new AgriStat("mutativity", MIN, MAX);
        //register default stats
        this.add(this.gainStat());
        this.add(this.growthStat());
        this.add(this.strengthStat());
        this.add(this.fertilityStat());
        this.add(this.resistanceStat());
        this.add(this.mutativityStat());
    }

    @Override
    public boolean add(@Nullable IAgriStat element) {
        boolean result = super.add(element);
        if(result) {
            // Also add a gene for this stat
            AgriGeneRegistry.getInstance().addGeneForStat(element);
        }
        return result;
    }

    @Override
    public boolean remove(@Nullable IAgriStat element) {
        if(this.has(element)) {
            // remove from own registry first
            boolean result = super.remove(element);
            if(element != null && result) {
                // remove associated gene after to avoid infinite loops
                AgriGeneRegistry.getInstance().removeGeneForStat(element);
            }
            return result;
        }
        return false;
    }

    @Override
    public int defaultMax() {
        return MAX;
    }

    @Override
    public int defaultMin() {
        return MIN;
    }

    @Override
    public IAgriStat gainStat() {
        return this.gain;
    }

    @Override
    public IAgriStat growthStat() {
        return this.growth;
    }

    @Override
    public IAgriStat strengthStat() {
        return this.strength;
    }

    @Override
    public IAgriStat fertilityStat() {
        return this.fertility;
    }

    @Override
    public IAgriStat resistanceStat() {
        return this.resistance;
    }

    @Override
    public IAgriStat mutativityStat() {
        return this.mutativity;
    }
}
