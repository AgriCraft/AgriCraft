package com.infinityraider.agricraft.core.stats;

import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import com.infinityraider.agricraft.core.genetics.AgriGeneRegistry;
import com.infinityraider.agricraft.impl.v1.AgriRegistry;

import javax.annotation.Nullable;

public class AgriStatRegistry extends AgriRegistry<IAgriStat> implements IAgriStatRegistry {
    private static final AgriStatRegistry INSTANCE = new AgriStatRegistry();

    public static final AgriStatRegistry getInstance() {
        return INSTANCE;
    }

    public static final Byte MIN = (byte) 1;
    public static final Byte MAX = (byte) 10;

    private final IAgriStat gain;
    private final IAgriStat growth;
    private final IAgriStat strength;
    private final IAgriStat fertility;
    private final IAgriStat resistance;

    private AgriStatRegistry() {
        //super constructor
        super("stats", IAgriStat.class);
        //initiate default stats
        this.gain = new AgriStatBase("gain", MIN, MAX);
        this.growth = new AgriStatBase("growth", MIN, MAX);
        this.strength = new AgriStatBase("strength", MIN, MAX);
        this.fertility = new AgriStatBase("fertility", MIN, MAX);
        this.resistance = new AgriStatBase("resistance", MIN, MAX);
        //register default stats
        this.add(this.gainStat());
        this.add(this.growthStat());
        this.add(this.strengthStat());
        this.add(this.fertilityStat());
        this.add(this.resistanceStat());
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
}
