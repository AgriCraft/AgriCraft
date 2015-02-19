package com.InfinityRaider.AgriCraft.farming.mutation;


public abstract class BaseStrategy implements ICrossOverStrategy {

    protected final MutationEngine engine;

    public BaseStrategy(MutationEngine mutationEngine) {
        this.engine = mutationEngine;
    }
}
