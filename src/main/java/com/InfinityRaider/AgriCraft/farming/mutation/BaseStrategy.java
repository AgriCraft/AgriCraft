package com.InfinityRaider.AgriCraft.farming.mutation;


import com.InfinityRaider.AgriCraft.api.v2.IStatCalculator;
import com.InfinityRaider.AgriCraft.api.v3.IMutationEngine;
import com.InfinityRaider.AgriCraft.farming.mutation.statcalculator.StatCalculator;

public abstract class BaseStrategy {
    protected IMutationEngine engine;
    protected final IStatCalculator calculator;

    public BaseStrategy(IMutationEngine mutationEngine) {
        this.engine = mutationEngine;
        this.calculator = StatCalculator.getInstance();
    }
}
