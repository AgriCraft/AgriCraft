package com.InfinityRaider.AgriCraft.farming.mutation;


import com.InfinityRaider.AgriCraft.api.v2.IStatCalculator;
import com.InfinityRaider.AgriCraft.farming.mutation.statcalculator.StatCalculator;

public abstract class BaseStrategy implements ICrossOverStrategy {
    protected final MutationEngine engine;
    protected final IStatCalculator calculator;

    public BaseStrategy(MutationEngine mutationEngine) {
        this.engine = mutationEngine;
        this.calculator = StatCalculator.getInstance();
    }
}
