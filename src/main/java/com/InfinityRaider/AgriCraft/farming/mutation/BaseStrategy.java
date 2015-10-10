package com.InfinityRaider.AgriCraft.farming.mutation;


import com.InfinityRaider.AgriCraft.farming.mutation.statcalculator.StatCalculator;

public abstract class BaseStrategy implements ICrossOverStrategy {
    protected final MutationEngine engine;
    protected final StatCalculator calculator;

    public BaseStrategy(MutationEngine mutationEngine) {
        this.engine = mutationEngine;
        this.calculator = StatCalculator.getInstance();
    }
}
