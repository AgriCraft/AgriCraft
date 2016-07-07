package com.infinityraider.agricraft.farming.mutation;


import com.infinityraider.agricraft.farming.mutation.statcalculator.StatCalculator;
import com.infinityraider.agricraft.api.stat.IAgriStatCalculator;

public abstract class BaseStrategy implements ICrossOverStrategy {
    protected final MutationEngine engine;
    protected final IAgriStatCalculator calculator;

    public BaseStrategy(MutationEngine mutationEngine) {
        this.engine = mutationEngine;
        this.calculator = StatCalculator.getInstance();
    }
}
