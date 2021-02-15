package com.infinityraider.agricraft.impl.v1.stats;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.stat.IAgriStat;
import com.infinityraider.agricraft.api.v1.stat.IAgriStatRegistry;
import com.infinityraider.agricraft.impl.v1.genetics.AgriGeneRegistry;
import com.infinityraider.agricraft.impl.v1.AgriRegistry;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;

public class AgriStatRegistry extends AgriRegistry<IAgriStat> implements IAgriStatRegistry {
    private static final AgriStatRegistry INSTANCE = new AgriStatRegistry();

    public static AgriStatRegistry getInstance() {
        return INSTANCE;
    }

    // register default stats
    static {
        INSTANCE.add(INSTANCE.gain);
        INSTANCE.add(INSTANCE.growth);
        INSTANCE.add(INSTANCE.strength);
        INSTANCE.add(INSTANCE.resistance);
        INSTANCE.add(INSTANCE.fertility);
        INSTANCE.add(INSTANCE.mutativity);
    }

    private final IAgriStat gain;
    private final IAgriStat growth;
    private final IAgriStat strength;
    private final IAgriStat resistance;
    private final IAgriStat fertility;
    private final IAgriStat mutativity;

    private AgriStatRegistry() {
        //super constructor
        super();
        //initiate default stats
        this.gain = new AgriStat("gain", this.defaultMin(), this.defaultMax(), new Vector3f(0, 0, 1));
        this.growth = new AgriStat("growth", this.defaultMin(), this.defaultMax(), new Vector3f(0, 1, 0));
        this.strength = new AgriStat("strength", this.defaultMin(), this.defaultMax(), new Vector3f(1, 0, 0));
        this.resistance = new AgriStat("resistance", this.defaultMin(), this.defaultMax(), new Vector3f(1, 1, 0));
        this.fertility = new AgriStat("fertility", this.defaultMin(), this.defaultMax(), new Vector3f(1, 0.5F, 0));
        this.mutativity = new AgriStat("mutativity", this.defaultMin(), this.defaultMax(), new Vector3f(0, 1, 1));
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
        return AgriCraft.instance.getConfig().getMaxStatsValue();
    }

    @Override
    public int defaultMin() {
        return AgriCraft.instance.getConfig().getMinStatsValue();
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
