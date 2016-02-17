package com.InfinityRaider.AgriCraft.farming.mutation;

import com.InfinityRaider.AgriCraft.farming.mutation.statcalculator.StatCalculator;

public class MutateStrategy extends BaseStrategy {
    public MutateStrategy(MutationEngine mutationEngine) {
        super(mutationEngine);
    }

    @Override
    public CrossOverResult executeStrategy() {
        Mutation[] crossOvers = MutationHandler.getCrossOvers(engine.getCrop().getMatureNeighbours());
        if (crossOvers.length > 0) {
            int index = engine.getRandom().nextInt(crossOvers.length);
            if (crossOvers[index].getResult().getItem() != null) {
                CrossOverResult result = CrossOverResult.fromMutation(crossOvers[index]);
                StatCalculator.setResultStats(result, engine.getCrop().getMatureNeighbours(), true);
                return result;
            }
        }
        return null;
    }
}
