package com.infinityraider.agricraft.farming.mutation;


import com.infinityraider.agricraft.api.v3.IStatCalculator;
import com.infinityraider.agricraft.farming.mutation.statcalculator.StatCalculator;

public abstract class BaseStrategy implements ICrossOverStrategy {
    protected final MutationEngine engine;
    protected final IStatCalculator calculator;

    public BaseStrategy(MutationEngine mutationEngine) {
        this.engine = mutationEngine;
        this.calculator = StatCalculator.getInstance();
    }
}
